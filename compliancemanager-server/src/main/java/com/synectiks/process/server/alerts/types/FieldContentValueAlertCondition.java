/*
 * */
package com.synectiks.process.server.alerts.types;

import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.indexer.results.ResultMessage;
import com.synectiks.process.server.indexer.results.SearchResult;
import com.synectiks.process.server.indexer.searches.Searches;
import com.synectiks.process.server.indexer.searches.Sorting;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.MessageSummary;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.streams.Stream;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FieldContentValueAlertCondition extends AbstractAlertCondition {
    private static final Logger LOG = LoggerFactory.getLogger(FieldContentValueAlertCondition.class);

    private final Searches searches;
    private final Configuration configuration;
    private final String field;
    private final String value;
    private final String query;

    public interface Factory extends AlertCondition.Factory {
        @Override
        FieldContentValueAlertCondition create(Stream stream,
                                               @Assisted("id") String id,
                                               DateTime createdAt,
                                               @Assisted("userid") String creatorUserId,
                                               Map<String, Object> parameters,
                                               @Assisted("title") @Nullable String title);

        @Override
        Config config();

        @Override
        Descriptor descriptor();
    }

    public static class Config implements AlertCondition.Config {
        public Config() {
        }

        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(
                    new TextField("field", "Field", "", "Field name that should be checked", ConfigurationField.Optional.NOT_OPTIONAL),
                    new TextField("value", "Value", "", "Value that the field should be checked against", ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addFields(AbstractAlertCondition.getDefaultConfigurationFields());

            return configurationRequest;
        }
    }

    public static class Descriptor extends AlertCondition.Descriptor {
        public Descriptor() {
            super(
                "Field Content Alert Condition",
                "Synectiks/",
                "This condition is triggered when the content of messages is equal to a defined value."
            );
        }
    }

    @AssistedInject
    public FieldContentValueAlertCondition(Searches searches,
                                           Configuration configuration,
                                           @Assisted Stream stream,
                                           @Nullable @Assisted("id") String id,
                                           @Assisted DateTime createdAt,
                                           @Assisted("userid") String creatorUserId,
                                           @Assisted Map<String, Object> parameters,
                                           @Assisted("title") @Nullable String title) {
        super(stream, id, Type.FIELD_CONTENT_VALUE.toString(), createdAt, creatorUserId, parameters, title);
        this.searches = searches;
        this.configuration = configuration;
        this.field = (String) parameters.get("field");
        this.value = (String) parameters.get("value");
        this.query = (String) parameters.getOrDefault(CK_QUERY, CK_QUERY_DEFAULT_VALUE);
    }

    @Override
    public CheckResult runCheck() {
        String filter = buildQueryFilter(stream.getId(), query);
        String query = field + ":\"" + value + "\"";
        Integer backlogSize = getBacklog();
        boolean backlogEnabled = false;
        int searchLimit = 1;

        if(backlogSize != null && backlogSize > 0) {
            backlogEnabled = true;
            searchLimit = backlogSize;
        }

        try {
            SearchResult result = searches.search(
                query,
                filter,
                RelativeRange.create(configuration.getAlertCheckInterval()),
                searchLimit,
                0,
                new Sorting(Message.FIELD_TIMESTAMP, Sorting.Direction.DESC)
            );

            final List<MessageSummary> summaries;
            if (backlogEnabled) {
                summaries = Lists.newArrayListWithCapacity(result.getResults().size());
                for (ResultMessage resultMessage : result.getResults()) {
                    final Message msg = resultMessage.getMessage();
                    summaries.add(new MessageSummary(resultMessage.getIndex(), msg));
                }
            } else {
                summaries = Collections.emptyList();
            }

            final long count = result.getTotalResults();

            final String resultDescription = "Stream received messages matching <" + query + "> "
                + "(Current grace time: " + grace + " minutes)";

            if (count > 0) {
                LOG.debug("Alert check <{}> found [{}] messages.", id, count);
                return new CheckResult(true, this, resultDescription, Tools.nowUTC(), summaries);
            } else {
                LOG.debug("Alert check <{}> returned no results.", id);
                return new NegativeCheckResult();
            }
        } catch (InvalidRangeParametersException e) {
            // cannot happen lol
            LOG.error("Invalid timerange.", e);
            return null;
        }
    }

    @Override
    public String getDescription() {
        return "field: " + field
                + ", value: " + value
                + ", grace: " + grace
                + ", repeat notifications: " + repeatNotifications;
    }
}
