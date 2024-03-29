/*
 * */
package com.synectiks.process.server.decorators;

import com.google.common.collect.ImmutableMap;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.decorators.SearchResponseDecorator;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class SyslogSeverityMapperDecorator implements SearchResponseDecorator {
    private static final String CK_SOURCE_FIELD = "source_field";
    private static final String CK_TARGET_FIELD = "target_field";

    // Map of numerical Syslog severities to names. See: https://tools.ietf.org/html/rfc3164#section-4.1.1
    private static final Map<String, String> SYSLOG_MAPPING = ImmutableMap.<String, String>builder()
            .put("0", "Emergency (0)")
            .put("1", "Alert (1)")
            .put("2", "Critical (2)")
            .put("3", "Error (3)")
            .put("4", "Warning (4)")
            .put("5", "Notice (5)")
            .put("6", "Informational (6)")
            .put("7", "Debug (7)")
            .build();


    private final String sourceField;
    private final String targetField;

    public interface Factory extends SearchResponseDecorator.Factory {
        @Override
        SyslogSeverityMapperDecorator create(Decorator decorator);

        @Override
        SyslogSeverityMapperDecorator.Config getConfig();

        @Override
        SyslogSeverityMapperDecorator.Descriptor getDescriptor();
    }

    public static class Config implements SearchResponseDecorator.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return new ConfigurationRequest() {
                {
                    addField(new TextField(
                            CK_SOURCE_FIELD,
                            "Source field",
                            "level",
                            "The message field which includes the numeric Syslog severity."
                    ));
                    addField(new TextField(
                            CK_TARGET_FIELD,
                            "Target field",
                            "severity",
                            "The message field that will be created with the mapped severity value."
                    ));
                }
            };
        }
    }

    public static class Descriptor extends SearchResponseDecorator.Descriptor {
        public Descriptor() {
            super("Syslog Severity Mapper", "http://docs.compliancemanager.org/", "Syslog Severity Mapper");
        }
    }

    @Inject
    public SyslogSeverityMapperDecorator(@Assisted Decorator decorator) {
        this.sourceField = (String) requireNonNull(decorator.config().get(CK_SOURCE_FIELD), CK_SOURCE_FIELD + " cannot be null");
        this.targetField = (String) requireNonNull(decorator.config().get(CK_TARGET_FIELD), CK_TARGET_FIELD + " cannot be null");
    }

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> summaries = searchResponse.messages().stream()
                .map(summary -> {
                    // Do not touch the message if the field does not exist.
                    if (!summary.message().containsKey(sourceField)) {
                        return summary;
                    }

                    final String level = String.valueOf(summary.message().get(sourceField));
                    final String severity = SYSLOG_MAPPING.get(level);

                    // If we cannot map the severity we do not touch the message.
                    if (severity == null) {
                        return summary;
                    }

                    final Message message = new Message(ImmutableMap.copyOf(summary.message()));

                    message.addField(targetField, severity);

                    return summary.toBuilder().message(message.getFields()).build();
                })
                .collect(Collectors.toList());

        return searchResponse.toBuilder().messages(summaries).build();
    }
}
