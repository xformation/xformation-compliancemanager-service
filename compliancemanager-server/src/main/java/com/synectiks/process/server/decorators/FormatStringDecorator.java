/*
 * */
package com.synectiks.process.server.decorators;

import com.floreysoft.jmte.Engine;
import com.floreysoft.jmte.template.Template;
import com.floreysoft.jmte.template.VariableDescription;
import com.google.common.collect.ImmutableMap;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.BooleanField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.decorators.SearchResponseDecorator;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class FormatStringDecorator implements SearchResponseDecorator {

    private static final String CK_FORMAT_STRING = "format_string";
    private static final String CK_REQUIRE_ALL_FIELDS = "require_all_fields";
    private static final String CK_TARGET_FIELD = "target_field";

    private final String targetField;
    private final Template template;
    private final boolean requireAllFields;
    private final List<VariableDescription> usedVariables;

    public interface Factory extends SearchResponseDecorator.Factory {
        @Override
        FormatStringDecorator create(Decorator decorator);

        @Override
        FormatStringDecorator.Config getConfig();

        @Override
        FormatStringDecorator.Descriptor getDescriptor();
    }

    public static class Config implements SearchResponseDecorator.Config {

        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return new ConfigurationRequest() {
                {
                    addField(new TextField(
                            CK_FORMAT_STRING,
                            "Format String",
                            "${source} - ${message}",
                            "The format string used to create the concatenated field."
                    ));
                    addField(new BooleanField(
                            CK_REQUIRE_ALL_FIELDS,
                            "Require all fields",
                            false,
                            "Check this if all fields in the format string need to be present in order to apply this decorator."));
                    addField(new TextField(
                            CK_TARGET_FIELD,
                            "Target field",
                            "message",
                            "The message field that will be created with the formatted string."
                    ));
                }
            };
        }
    }

    public static class Descriptor extends SearchResponseDecorator.Descriptor {
        public Descriptor() {
            super("Format String", "http://docs.compliancemanager.org/", "Format string");
        }
    }

    @Inject
    public FormatStringDecorator(@Assisted Decorator decorator, Engine templateEngine) {
        final String formatString = (String) requireNonNull(decorator.config().get(CK_FORMAT_STRING),
                                                            CK_FORMAT_STRING + " cannot be null");
        this.targetField = (String) requireNonNull(decorator.config().get(CK_TARGET_FIELD),
                                                   CK_TARGET_FIELD + " cannot be null");
        requireAllFields = (boolean) requireNonNull(decorator.config().get(CK_REQUIRE_ALL_FIELDS),
                                                    CK_REQUIRE_ALL_FIELDS + " cannot be null");
        template = requireNonNull(templateEngine, "templateEngine").getTemplate(formatString);
        usedVariables = template.getUsedVariableDescriptions();
    }

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> summaries = searchResponse.messages().stream()
                .map(summary -> {
                    if (requireAllFields && !usedVariables.stream().allMatch(variable -> summary.message().containsKey(variable.name))) {
                        return summary;
                    }
                    final String formattedString = template.transform(summary.message(), Locale.ENGLISH);

                    if (formattedString == null) {
                        return summary;
                    }

                    final Message message = new Message(ImmutableMap.copyOf(summary.message()));
                    message.addField(targetField, formattedString);
                    return summary.toBuilder().message(message.getFields()).build();
                })
                .collect(Collectors.toList());

        return searchResponse.toBuilder().messages(summaries).build();
    }
}
