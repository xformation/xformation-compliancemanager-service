/*
 * */
package com.synectiks.process.server.plugin.streams;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.synectiks.process.server.plugin.database.Persisted;

import java.util.Map;

@JsonAutoDetect
public interface StreamRule extends Persisted {
    @Override
    String getId();

    StreamRuleType getType();

    String getField();

    String getValue();

    Boolean getInverted();

    String getStreamId();

    String getContentPack();

    String getDescription();

    void setType(StreamRuleType type);

    void setField(String field);

    void setValue(String value);

    void setInverted(Boolean inverted);

    void setContentPack(String contentPack);

    void setDescription(String description);

    @Override
    Map<String, Object> asMap();
}
