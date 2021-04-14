/*
 * */
package com.synectiks.process.server.rest.models.messages.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MessageParseRequest {
    @JsonProperty
    public abstract String message();

    @JsonProperty
    public abstract String codec();

    @JsonProperty
    public abstract String remoteAddress();

    @JsonProperty
    @Nullable
    public abstract Map<String, Object> configuration();

    @JsonCreator
    public static MessageParseRequest create(@JsonProperty("message") String message,
                                             @JsonProperty("codec") String codec,
                                             @JsonProperty("remote_address") String remoteAddress,
                                             @JsonProperty("configuration") Map<String, Object> configuration) {
        return new AutoValue_MessageParseRequest(message, codec, remoteAddress, configuration);
    }
}
