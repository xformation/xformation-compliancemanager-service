/*
 * */
package com.synectiks.process.common.plugins.cef.codec;

import com.synectiks.process.server.cef.CEFParser;
import com.synectiks.process.server.cef.CEFParserFactory;
import com.google.common.primitives.Ints;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.common.plugins.cef.parser.CEFMapping;
import com.synectiks.process.common.plugins.cef.parser.MappedMessage;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog.SyslogUtils;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.ResolvableInetSocketAddress;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.BooleanField;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.codecs.Codec;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;
import com.synectiks.process.server.plugin.journal.RawMessage;
import com.synectiks.process.server.shared.SuppressForbidden;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

@SuppressForbidden("Intentionally use system default timezone")
public class CEFCodec implements Codec {
    public static final String NAME = "CEF";

    private static final Logger LOG = LoggerFactory.getLogger(CEFCodec.class);

    private static final Pattern SYSLOG_PREFIX = Pattern.compile("^<(?<pri>\\d+)>(?<msg>.*)$");

    private static final String CK_TIMEZONE = "timezone";
    private static final String CK_LOCALE = "locale";
    private static final String CK_USE_FULL_NAMES = "use_full_names";

    private static final DateTimeZone DEFAULT_TIMEZONE = DateTimeZone.getDefault();

    private final Configuration configuration;
    private final DateTimeZone timezone;
    private final Locale locale;
    private final boolean useFullNames;
    private final CEFParser parser;

    @AssistedInject
    public CEFCodec(@Assisted Configuration configuration) {
        this.configuration = configuration;
        this.parser = CEFParserFactory.create();

        DateTimeZone timezone;
        try {
            timezone = DateTimeZone.forID(configuration.getString(CK_TIMEZONE));
        } catch (Exception e) {
            LOG.warn("Could not configure CEF input timezone. Falling back to local default. Please check the error message:", e);
            timezone = DEFAULT_TIMEZONE;
        }
        this.timezone = timezone;
        this.locale = Locale.forLanguageTag(configuration.getString(CK_LOCALE, ""));

        this.useFullNames = configuration.getBoolean(CK_USE_FULL_NAMES);
    }

    @Nullable
    @Override
    public Message decode(@Nonnull RawMessage rawMessage) {
        final String s = new String(rawMessage.getPayload(), StandardCharsets.UTF_8);
        final Matcher matcher = SYSLOG_PREFIX.matcher(s);

        if (matcher.find()) {
            final String priString = matcher.group("pri");
            final Integer pri = Ints.tryParse(priString);
            final Map<String, Object> syslogFields = new HashMap<>();
            if (pri != null) {
                final int facility = SyslogUtils.facilityFromPriority(pri);
                syslogFields.put("level", SyslogUtils.levelFromPriority(pri));
                syslogFields.put("facility", SyslogUtils.facilityToString(facility));
            }

            final String msg = matcher.group("msg");
            final Message message = decodeCEF(rawMessage, msg);
            message.addFields(syslogFields);

            return message;
        } else {
            return decodeCEF(rawMessage, s);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    protected Message decodeCEF(@Nonnull RawMessage rawMessage, String s) {
        try {
            final MappedMessage cef = new MappedMessage(parser.parse(s, timezone.toTimeZone(), locale), useFullNames);

            // Build standard message.
            Message result = new Message(buildMessageSummary(cef), decideSource(cef, rawMessage), new DateTime(cef.timestamp()));

            // Add all extensions.
            result.addFields(cef.mappedExtensions());

            // Add standard CEF fields.
            result.addField("device_vendor", cef.deviceVendor());
            result.addField("device_product", cef.deviceProduct());
            result.addField("device_version", cef.deviceVersion());
            result.addField("event_class_id", cef.deviceEventClassId());
            result.addField("name", cef.name());
            result.addField("severity", cef.severity());

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Could not decode CEF message.", e);
        }
    }

    protected String buildMessageSummary(com.synectiks.process.server.cef.Message cef) {
        return cef.deviceProduct() + ": [" + cef.deviceEventClassId() + ", " + cef.severity() + "] " + cef.name();
    }

    protected String decideSource(MappedMessage cef, RawMessage raw) {
        // Try getting the host name from the CEF extension "deviceAddress"/"dvc"
        final Map<String, Object> fields = cef.mappedExtensions();
        if (fields != null && !fields.isEmpty()) {
            final String deviceAddress = (String) fields.getOrDefault(CEFMapping.dvc.getFullName(), fields.get(CEFMapping.dvc.getKeyName()));
            if (!isNullOrEmpty(deviceAddress)) {
                return deviceAddress;
            }
        }

        // Try getting the hostname from the CEF message metadata (e. g. syslog)
        if (!isNullOrEmpty(cef.host())) {
            return cef.host();
        }

        // Use raw message source information if we were not able to parse a source from the CEF extensions.
        final ResolvableInetSocketAddress address = raw.getRemoteAddress();
        final InetSocketAddress remoteAddress;
        if (address == null) {
            remoteAddress = null;
        } else {
            remoteAddress = address.getInetSocketAddress();
        }

        return remoteAddress == null ? "unknown" : remoteAddress.getAddress().toString();
    }

    @Nullable
    @Override
    public CodecAggregator getAggregator() {
        return null;
    }

    @Nonnull
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @FactoryClass
    public interface Factory extends Codec.Factory<CEFCodec> {
        @Override
        CEFCodec create(Configuration configuration);

        @Override
        Config getConfig();
    }

    @ConfigClass
    public static class Config implements Codec.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            ConfigurationRequest cr = new ConfigurationRequest();

            cr.addField(new TextField(
                    CK_TIMEZONE,
                    "Timezone",
                    DEFAULT_TIMEZONE.getID(),
                    "Timezone of the timestamps in CEF messages. Set this to the local timezone if in doubt. Format example: \"+01:00\" or \"America/Chicago\"",
                    ConfigurationField.Optional.NOT_OPTIONAL
            ));
            cr.addField(new TextField(
                    CK_LOCALE,
                    "Locale",
                    "",
                    "Locale to use for parsing the timestamps of CEF messages. Set this to english if in doubt. Format example: \"en\" or \"en_US\"",
                    ConfigurationField.Optional.OPTIONAL
            ));
            cr.addField(new BooleanField(
                    CK_USE_FULL_NAMES,
                    "Use full field names",
                    false,
                    "Use full field names in CEF messages (as defined in the CEF specification)"
            ));

            return cr;
        }

        @Override
        public void overrideDefaultValues(@Nonnull ConfigurationRequest cr) {
        }
    }
}
