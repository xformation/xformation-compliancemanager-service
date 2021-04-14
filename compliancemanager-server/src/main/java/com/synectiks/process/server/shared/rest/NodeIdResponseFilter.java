/*
 * */
package com.synectiks.process.server.shared.rest;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import com.synectiks.process.server.plugin.system.NodeId;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class NodeIdResponseFilter implements ContainerResponseFilter {
    private final NodeId nodeId;

    @Inject
    public NodeIdResponseFilter(NodeId nodeId) {
        this.nodeId = checkNotNull(nodeId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("X-alertmanager-Node-ID", nodeId.toString());
    }
}
