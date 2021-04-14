/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NetFlowV9ScopeDef {
    public static final int SYSTEM = 1;
    public static final int INTERFACE = 2;
    public static final int LINECARD = 3;
    public static final int NETFLOW_CACHE = 4;
    public static final int TEMPLATE = 5;

    public abstract int type();

    public abstract int length();

    public static NetFlowV9ScopeDef create(int type, int length) {
        return new AutoValue_NetFlowV9ScopeDef(type, length);
    }
}
