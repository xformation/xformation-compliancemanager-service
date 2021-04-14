/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
public abstract class NetFlowV9OptionTemplate {
    public abstract int templateId();

    public abstract ImmutableList<NetFlowV9ScopeDef> scopeDefs();

    public abstract ImmutableList<NetFlowV9FieldDef> optionDefs();

    public static NetFlowV9OptionTemplate create(int templateId,
                                                 List<NetFlowV9ScopeDef> scopeDefs,
                                                 List<NetFlowV9FieldDef> optionDefs) {
        return new AutoValue_NetFlowV9OptionTemplate(templateId, ImmutableList.copyOf(scopeDefs), ImmutableList.copyOf(optionDefs));
    }
}
