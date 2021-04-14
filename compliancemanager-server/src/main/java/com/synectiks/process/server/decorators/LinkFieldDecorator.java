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

import org.apache.commons.validator.routines.UrlValidator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class LinkFieldDecorator implements SearchResponseDecorator {

    public static final String CK_LINK_FIELD = "link_field";
    // UrlValidator.ALLOW_LOCAL_URLS allows local links to be permitted such as http://my-local-server
    // Some users may reference such local URLs, and there should be no issue with doing so.
    private final static UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS + UrlValidator.ALLOW_2_SLASHES);

    private final String linkField;

    public interface Factory extends SearchResponseDecorator.Factory {
        @Override
        LinkFieldDecorator create(Decorator decorator);

        @Override
        LinkFieldDecorator.Config getConfig();

        @Override
        LinkFieldDecorator.Descriptor getDescriptor();
    }

    public static class Config implements SearchResponseDecorator.Config {

        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return new ConfigurationRequest() {
                {
                    addField(new TextField(
                            CK_LINK_FIELD,
                            "Link field",
                            "message",
                            "The field that will be transformed into a hyperlink."
                    ));
                }
            };
        }
    }

    public static class Descriptor extends SearchResponseDecorator.Descriptor {
        public Descriptor() {
            super("Hyperlink String", "http://docs.alertmanager.org/", "Hyperlink string");
        }
    }

    @Inject
    public LinkFieldDecorator(@Assisted Decorator decorator) {
        this.linkField = (String) requireNonNull(decorator.config().get(CK_LINK_FIELD),
                                                   CK_LINK_FIELD + " cannot be null");
    }

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> summaries = searchResponse.messages().stream()
                .map(summary -> {
                    if (!summary.message().containsKey(linkField)) {
                      return summary;
                    }
                    final Message message = new Message(ImmutableMap.copyOf(summary.message()));
                    final String href = (String) summary.message().get(linkField);
                    if (isValidUrl(href)) {
                        final Map<String, String> decoratedField = new HashMap<>();
                        decoratedField.put("type", "a");
                        decoratedField.put("href", href);
                        message.addField(linkField, decoratedField);
                    } else {
                        message.addField(linkField, href);
                    }
                    return summary.toBuilder().message(message.getFields()).build();
                })
                .collect(Collectors.toList());

        return searchResponse.toBuilder().messages(summaries).build();
    }

    /**
     * @param url a String URL.
     * @return true if the URL is valid and false if the URL is invalid.
     *
     * All URLS that do not start with the protocol "http" or "https" protocol scheme are considered invalid.
     * All other non-URL text strings will be considered invalid. This includes inline javascript expressions such as:
     *  - javascript:...
     *  - alert()
     *  - or any other javascript expressions.
     */
    private boolean isValidUrl(String url) {
        return URL_VALIDATOR.isValid(url);
    }
}
