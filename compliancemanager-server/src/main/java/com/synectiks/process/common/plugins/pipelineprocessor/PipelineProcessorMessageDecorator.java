/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineDao;
import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineService;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.ConfigurationStateUpdater;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.PipelineInterpreter;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.listeners.NoopInterpreterListener;
import com.synectiks.process.server.decorators.Decorator;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.DropdownField;
import com.synectiks.process.server.plugin.decorators.SearchResponseDecorator;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PipelineProcessorMessageDecorator implements SearchResponseDecorator {
    private static final String CONFIG_FIELD_PIPELINE = "pipeline";

    private final PipelineInterpreter pipelineInterpreter;
    private final ConfigurationStateUpdater pipelineStateUpdater;
    private final ImmutableSet<String> pipelines;

    public interface Factory extends SearchResponseDecorator.Factory {
        @Override
        PipelineProcessorMessageDecorator create(Decorator decorator);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Config implements SearchResponseDecorator.Config {
        private final PipelineService pipelineService;

        @Inject
        public Config(PipelineService pipelineService) {
            this.pipelineService = pipelineService;
        }

        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final Map<String, String> pipelineOptions = this.pipelineService.loadAll().stream()
                    .sorted((o1, o2) -> o1.title().compareTo(o2.title()))
                    .collect(Collectors.toMap(PipelineDao::id, PipelineDao::title));
            return new ConfigurationRequest() {{
                addField(new DropdownField(CONFIG_FIELD_PIPELINE,
                        "Pipeline",
                        "",
                        pipelineOptions,
                        "Which pipeline to use for message decoration",
                        ConfigurationField.Optional.NOT_OPTIONAL));
            }};
        };
    }

    public static class Descriptor extends SearchResponseDecorator.Descriptor {
        public Descriptor() {
            super("Pipeline Processor Decorator", "http://docs.graylog.org/en/2.0/pages/pipelines.html", "Pipeline Processor Decorator");
        }
    }

    @Inject
    public PipelineProcessorMessageDecorator(PipelineInterpreter pipelineInterpreter,
                                             ConfigurationStateUpdater pipelineStateUpdater,
                                             @Assisted Decorator decorator) {
        this.pipelineInterpreter = pipelineInterpreter;
        this.pipelineStateUpdater = pipelineStateUpdater;
        final String pipelineId = (String)decorator.config().get(CONFIG_FIELD_PIPELINE);
        if (Strings.isNullOrEmpty(pipelineId)) {
            this.pipelines = ImmutableSet.of();
        } else {
            this.pipelines = ImmutableSet.of(pipelineId);
        }
    }

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> results = new ArrayList<>();
        if (pipelines.isEmpty()) {
            return searchResponse;
        }
        searchResponse.messages().forEach((inMessage) -> {
            final Message message = new Message(inMessage.message());
            final List<Message> additionalCreatedMessages = pipelineInterpreter.processForPipelines(message,
                    pipelines,
                    new NoopInterpreterListener(),
                    pipelineStateUpdater.getLatestState());

            results.add(ResultMessageSummary.create(inMessage.highlightRanges(), message.getFields(), inMessage.index()));
            additionalCreatedMessages.forEach((additionalMessage) -> {
                // TODO: pass proper highlight ranges. Need to rebuild them for new messages.
                results.add(ResultMessageSummary.create(
                        ImmutableMultimap.of(),
                        additionalMessage.getFields(),
                        "[created from decorator]"
                ));
            });
        });

        return searchResponse.toBuilder().messages(results).build();
    }
}
