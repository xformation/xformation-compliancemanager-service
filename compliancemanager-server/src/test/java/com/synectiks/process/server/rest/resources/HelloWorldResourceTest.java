/*
 * */
package com.synectiks.process.server.rest.resources;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.configuration.HttpConfiguration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.cluster.ClusterId;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.rest.models.HelloWorldResponse;
import com.synectiks.process.server.rest.resources.HelloWorldResource;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HelloWorldResourceTest extends RestResourceBaseTest {
    private static final String CK_CLUSTER_ID = "dummyclusterid";
    private static final String CK_NODE_ID = "dummynodeid";

    private HelloWorldResource helloWorldResource;
    private NodeId nodeId;
    private ClusterConfigService clusterConfigService;

    @Before
    public void setUp() throws Exception {
        this.nodeId = mock(NodeId.class);
        this.clusterConfigService = mock(ClusterConfigService.class);
        this.helloWorldResource = new HelloWorldResource(nodeId, clusterConfigService);

        when(clusterConfigService.getOrDefault(eq(ClusterId.class), any(ClusterId.class))).thenReturn(ClusterId.create(CK_CLUSTER_ID));
        when(nodeId.toString()).thenReturn(CK_NODE_ID);
    }

    @Test
    public void rootResourceShouldReturnGeneralStats() throws Exception {
        final HelloWorldResponse helloWorldResponse = this.helloWorldResource.helloWorld();

        assertThat(helloWorldResponse).isNotNull();

        assertThat(helloWorldResponse.clusterId()).isEqualTo(CK_CLUSTER_ID);
        assertThat(helloWorldResponse.nodeId()).isEqualTo(CK_NODE_ID);
    }

    @Test
    public void rootResourceShouldRedirectToWebInterfaceIfHtmlIsRequested() throws Exception {
        final Response response = helloWorldResource.redirectToWebConsole();

        assertThat(response).isNotNull();

        final String locationHeader = response.getHeaderString("Location");
        assertThat(locationHeader).isNotNull().isEqualTo(HttpConfiguration.PATH_WEB);
    }
}
