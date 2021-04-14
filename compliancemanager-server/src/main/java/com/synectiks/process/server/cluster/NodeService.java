/*
 * */
package com.synectiks.process.server.cluster;

import com.synectiks.process.server.plugin.database.PersistedService;
import com.synectiks.process.server.plugin.system.NodeId;

import java.net.URI;
import java.util.Map;

public interface NodeService extends PersistedService {
    String registerServer(String nodeId, boolean isMaster, URI httpPublishUri, String hostname);

    Node byNodeId(String nodeId) throws NodeNotFoundException;

    Node byNodeId(NodeId nodeId) throws NodeNotFoundException;

    Map<String, Node> allActive(Node.Type type);

    Map<String, Node> allActive();

    void dropOutdated();

    void markAsAlive(Node node, boolean isMaster, String restTransportAddress);

    void markAsAlive(Node node, boolean isMaster, URI restTransportAddress);

    boolean isOnlyMaster(NodeId nodeIde);

    boolean isAnyMasterPresent();
}
