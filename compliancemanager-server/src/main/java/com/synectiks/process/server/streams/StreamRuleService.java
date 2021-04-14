/*
 * */
package com.synectiks.process.server.streams;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.PersistedService;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.rest.resources.streams.rules.requests.CreateStreamRuleRequest;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StreamRuleService extends PersistedService {
    StreamRule load(String id) throws NotFoundException;

    List<StreamRule> loadForStream(Stream stream);

    StreamRule create(Map<String, Object> data);

    StreamRule create(@Nullable String streamId, CreateStreamRuleRequest request);

    StreamRule copy(@Nullable String streamId, StreamRule streamRule);

    String save(StreamRule streamRule) throws ValidationException;

    Set<String> save(Collection<StreamRule> streamRules) throws ValidationException;

    int destroy(StreamRule streamRule);

    List<StreamRule> loadForStreamId(String streamId);

    Map<String, List<StreamRule>> loadForStreamIds(Collection<String> streamIds);

    /**
     * @return the total number of stream rules
     */
    long totalStreamRuleCount();

    /**
     * @param streamId the stream ID
     * @return the number of stream rules for the specified stream
     */
    long streamRuleCount(String streamId);

    /**
     * @return the number of stream rules grouped by stream
     */
    Map<String, Long> streamRuleCountByStream();
}
