/*
 * */
package com.synectiks.process.server.shared.bindings.providers;

import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.system.NodeId;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class NodeIdProvider implements Provider<NodeId> {
    private final ServerStatus serverStatus;

    @Inject
    public NodeIdProvider(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    @Override
    public NodeId get() {
        return serverStatus.getNodeId();
    }
}
