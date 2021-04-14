/*
 * */
package com.synectiks.process.common.testing.graylognode;

import org.apache.commons.lang.ArrayUtils;
import org.testcontainers.containers.Network;

import java.util.Arrays;

public class NodeContainerConfig {

    static final int API_PORT = 9000;
    static final int DEBUG_PORT = 5005;

    public final Network network;
    public final String mongoDbUri;
    public final String elasticsearchVersion;
    public final String elasticsearchUri;
    public final int[] extraPorts;
    public final boolean enableDebugging;
    public final boolean skipPackaging;

    public static NodeContainerConfig create(Network network, String mongoDbUri, String elasticsearchUri, String elasticsearchVersion, int[] extraPorts) {
        return new NodeContainerConfig(network, mongoDbUri, elasticsearchUri, elasticsearchVersion, extraPorts);
    }

    public NodeContainerConfig(Network network, String mongoDbUri, String elasticsearchUri, String elasticsearchVersion, int[] extraPorts) {
        this.network = network;
        this.mongoDbUri = mongoDbUri;
        this.elasticsearchUri = elasticsearchUri;
        this.elasticsearchVersion = elasticsearchVersion;
        this.extraPorts = extraPorts == null ? new int[0] : extraPorts;
        this.enableDebugging = flagFromEnvVar("ALERTMANAGER_IT_DEBUG_SERVER");
        this.skipPackaging = flagFromEnvVar("ALERTMANAGER_IT_SKIP_PACKAGING");
    }

    private static boolean flagFromEnvVar(String flagName) {
        String flag = System.getenv(flagName);
        return flag != null && flag.equalsIgnoreCase("true");
    }

    public Integer[] portsToExpose() {
        int[] allPorts = ArrayUtils.add(extraPorts, 0, API_PORT);

        if (enableDebugging) {
            allPorts = ArrayUtils.add(allPorts, 0, DEBUG_PORT);
        }

        return Arrays.stream(allPorts).boxed().toArray(Integer[]::new);
    }
}
