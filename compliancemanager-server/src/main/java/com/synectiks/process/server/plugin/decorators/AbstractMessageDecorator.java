/*
 * */
package com.synectiks.process.server.plugin.decorators;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractMessageDecorator implements SearchResponseDecorator {
    abstract Message decorate(Message message);

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> results = searchResponse.messages().stream()
            .map(resultMessageSummary -> {
                final Message decoratedMessage = decorate(new Message(resultMessageSummary.message()));
                return ResultMessageSummary.create(resultMessageSummary.highlightRanges(), decoratedMessage.getFields(), resultMessageSummary.index());
            })
            .collect(Collectors.toList());

        return searchResponse.toBuilder().messages(results).build();
    }
}
