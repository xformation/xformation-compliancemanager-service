/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@JsonAutoDetect
@AutoValue
public abstract class NetFlowV9Template {
    @JsonProperty("template_id")
    public abstract int templateId();

    @JsonProperty("field_count")
    public abstract int fieldCount();

    @JsonProperty("definitions")
    public abstract ImmutableList<NetFlowV9FieldDef> definitions();

    @JsonCreator
    public static NetFlowV9Template create(@JsonProperty("template_id") int templateId,
                                           @JsonProperty("field_count") int fieldCount,
                                           @JsonProperty("definitions") List<NetFlowV9FieldDef> definitions) {
        return new AutoValue_NetFlowV9Template(templateId, fieldCount, ImmutableList.copyOf(definitions));
    }

}
