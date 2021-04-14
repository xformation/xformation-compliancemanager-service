/*
 * */
package com.synectiks.process.server.indexer.indexset;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class V20161216123500_Succeeded {
    @JsonCreator
    public static V20161216123500_Succeeded create() {
        return new AutoValue_V20161216123500_Succeeded();
    }
}
