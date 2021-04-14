/*
 * */
package com.synectiks.process.common.testing.graylognode;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import static com.synectiks.process.common.testing.graylognode.NodeContainerConfig.API_PORT;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NodeInstance {

    private static final Logger LOG = LoggerFactory.getLogger(NodeInstance.class);

    private final GenericContainer<?> container;

    public static NodeInstance createStarted(Network network, String mongoDbUri, String elasticsearchUri, String elasticsearchVersion, int[] extraPorts) {
        NodeContainerConfig config = NodeContainerConfig.create(network, mongoDbUri, elasticsearchUri, elasticsearchVersion, extraPorts);
        GenericContainer<?> container = NodeContainerFactory.buildContainer(config);
        return new NodeInstance(container);
    }

    public NodeInstance(GenericContainer<?> container) {
        this.container = container;
    }

    public void restart() {
        Stopwatch sw = Stopwatch.createStarted();
        container.stop();
        container.start();
        sw.stop();
        LOG.info("Restarted node container in " + sw.elapsed(TimeUnit.SECONDS));
    }

    public String uri() {
        return String.format(Locale.US, "http://%s", container.getContainerIpAddress());
    }

    public int apiPort() {
        return mappedPortFor(API_PORT);
    }

    public int mappedPortFor(int originalPort) {
        return container.getMappedPort(originalPort);
    }

    public void printLog() {
        System.out.println(container.getLogs());
    }
}
