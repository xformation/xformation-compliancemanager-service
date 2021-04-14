/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;

@AutoValue
public abstract class NetFlowV9Packet {
    public abstract NetFlowV9Header header();

    public abstract ImmutableList<NetFlowV9Template> templates();

    @Nullable
    public abstract NetFlowV9OptionTemplate optionTemplate();

    public abstract ImmutableList<NetFlowV9BaseRecord> records();

    public abstract long dataLength();

    public static NetFlowV9Packet create(NetFlowV9Header header,
                                         List<NetFlowV9Template> templates,
                                         @Nullable NetFlowV9OptionTemplate optionTemplate,
                                         List<NetFlowV9BaseRecord> records,
                                         long dataLength) {
        return new AutoValue_NetFlowV9Packet(header, ImmutableList.copyOf(templates), optionTemplate, ImmutableList.copyOf(records), dataLength);
    }

    @Override
    public String toString() {
        return header().toString();
    }
}
