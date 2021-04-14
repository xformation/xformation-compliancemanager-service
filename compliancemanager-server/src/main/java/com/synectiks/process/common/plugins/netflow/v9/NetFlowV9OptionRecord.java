/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@AutoValue
public abstract class NetFlowV9OptionRecord implements NetFlowV9BaseRecord {
    @Override
    public abstract ImmutableMap<String, Object> fields();

    public abstract ImmutableMap<Integer, Object> scopes();

    public static NetFlowV9OptionRecord create(Map<String, Object> fields, Map<Integer, Object> scopes) {
        return new AutoValue_NetFlowV9OptionRecord(ImmutableMap.copyOf(fields), ImmutableMap.copyOf(scopes));
    }
}