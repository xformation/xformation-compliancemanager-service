/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class LiteralWhitelistEntry implements WhitelistEntry {
    @JsonCreator
    public static LiteralWhitelistEntry create(@JsonProperty("id") String id, @JsonProperty("title") String title,
            @JsonProperty("value") String value) {
        return new AutoValue_LiteralWhitelistEntry(id, Type.LITERAL, title, value);
    }

    @Override
    public boolean isWhitelisted(String url) {
        return value().equals(url);
    }
}
