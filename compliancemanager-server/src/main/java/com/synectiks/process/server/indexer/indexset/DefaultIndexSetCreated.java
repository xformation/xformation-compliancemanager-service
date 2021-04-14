/*
 * */
package com.synectiks.process.server.indexer.indexset;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class DefaultIndexSetCreated {
    @JsonCreator
    public static DefaultIndexSetCreated create() {
        return new AutoValue_DefaultIndexSetCreated();
    }
}