/*
 * */
package com.synectiks.process.server.cluster;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.database.Persisted;

public interface Node extends Persisted {
    enum Type {
        SERVER
    }

    String getNodeId();

    boolean isMaster();

    String getTransportAddress();

    DateTime getLastSeen();

    String getShortNodeId();

    Node.Type getType();

    String getHostname();
}
