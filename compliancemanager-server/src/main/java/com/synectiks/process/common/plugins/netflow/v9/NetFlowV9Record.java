/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@AutoValue
public abstract class NetFlowV9Record implements NetFlowV9BaseRecord {
    @Override
    public abstract ImmutableMap<String, Object> fields();

    public static NetFlowV9Record create(Map<String, Object> fields) {
        return new AutoValue_NetFlowV9Record(ImmutableMap.copyOf(fields));
    }
}
