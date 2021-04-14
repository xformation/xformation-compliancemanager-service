/*
 * */
package com.synectiks.process.common.plugins.netflow.v5;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

@AutoValue
public abstract class NetFlowV5Packet {
    public abstract NetFlowV5Header header();

    public abstract ImmutableList<NetFlowV5Record> records();

    public abstract long dataLength();

    public static NetFlowV5Packet create(NetFlowV5Header header, List<NetFlowV5Record> records, long dataLength) {
        return new AutoValue_NetFlowV5Packet(header, ImmutableList.copyOf(records), dataLength);
    }
}
