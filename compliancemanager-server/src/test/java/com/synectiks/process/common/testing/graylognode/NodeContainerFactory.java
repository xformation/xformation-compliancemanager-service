/*
 * */
package com.synectiks.process.common.testing.graylognode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import com.synectiks.process.common.testing.PropertyLoader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static com.synectiks.process.common.testing.ResourceUtil.resourceToTmpFile;
import static com.synectiks.process.common.testing.graylognode.NodeContainerConfig.API_PORT;
import static com.synectiks.process.common.testing.graylognode.NodeContainerConfig.DEBUG_PORT;
import static java.time.temporal.ChronoUnit.SECONDS;

public class NodeContainerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NodeContainerFactory.class);

    @SuppressWarnings("OctalInteger")
    private static final int EXECUTABLE_MODE = 0100755;
    // sha2 for password "admin"
    private static final String ADMIN_PW_SHA2 = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";

    public static GenericContainer<?> buildContainer(NodeContainerConfig config) {
        if (!config.skipPackaging) {
            MavenPackager.packageJarIfNecessary(property("server_project_dir"));
        } else {
            LOG.info("Skipping packaging");
        }
        ImageFromDockerfile image = createImage(config);

        return createRunningContainer(config, image);
    }

    private static ImageFromDockerfile createImage(NodeContainerConfig config) {
        // testcontainers only allows passing permissions if you pass a `File`
        File entrypointScript = resourceToTmpFile("com/synectiks/process/common/testing/graylognode/docker-entrypoint.sh");

        ImageFromDockerfile image = new ImageFromDockerfile()
                .withFileFromClasspath("Dockerfile", "com/synectiks/process/common/testing/graylognode/Dockerfile")
                // set mode here explicitly, because file system permissions can get lost when executing from maven
                .withFileFromFile("docker-entrypoint.sh", entrypointScript, EXECUTABLE_MODE)
                .withFileFromPath("server.conf", pathTo("alertmanager_config"))
                .withFileFromClasspath("log4j2.xml", "log4j2.xml")
                .withFileFromPath("sigar", pathTo("sigar_dir"));
        if (config.enableDebugging) {
            image.withBuildArg("DEBUG_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:5005");
        }
        return image;
    }

    private static GenericContainer<?> createRunningContainer(NodeContainerConfig config, ImageFromDockerfile image) {
        String graylogHome = "/opt/alertmanager";

        GenericContainer<?> container = new GenericContainer<>(image)
                .withFileSystemBind(property("server_jar"), graylogHome + "/graylog.jar", BindMode.READ_ONLY)
                .withFileSystemBind(property("storage_module_elasticsearch6_jar"), graylogHome + "/plugin/graylog-storage-elasticsearch6.jar", BindMode.READ_ONLY)
                .withFileSystemBind(property("storage_module_elasticsearch7_jar"), graylogHome + "/plugin/graylog-storage-elasticsearch7.jar", BindMode.READ_ONLY)
                .withFileSystemBind(property("aws_plugin_jar"), graylogHome + "/plugin/graylog-plugin-aws.jar", BindMode.READ_ONLY)
                .withFileSystemBind(property("threatintel_plugin_jar"), graylogHome + "/plugin/graylog-plugin-threatintel.jar", BindMode.READ_ONLY)
                .withFileSystemBind(property("collector_plugin_jar"), graylogHome + "/plugin/graylog-plugin-collector.jar", BindMode.READ_ONLY)
                .withNetwork(config.network)
                .withEnv("DEVELOPMENT", "true")
                .withEnv("GRAYLOG_MONGODB_URI", config.mongoDbUri)
                .withEnv("GRAYLOG_ELASTICSEARCH_HOSTS", config.elasticsearchUri)
                .withEnv("GRAYLOG_ELASTICSEARCH_VERSION", config.elasticsearchVersion)
                .withEnv("GRAYLOG_PASSWORD_SECRET", "M4lteserKreuzHerrStrack?")
                .withEnv("GRAYLOG_NODE_ID_FILE", "data/config/node-id")
                .withEnv("GRAYLOG_HTTP_BIND_ADDRESS", "0.0.0.0:" + API_PORT)
                .withEnv("GRAYLOG_ROOT_PASSWORD_SHA2", ADMIN_PW_SHA2)
                .withEnv("GRAYLOG_LB_RECOGNITION_PERIOD_SECONDS", "0")
                .withEnv("GRAYLOG_VERSIONCHECKS", "false")
                .waitingFor(Wait.forLogMessage(".*Graylog server up and running.*", 1))
                .withExposedPorts(config.portsToExpose())
                .withStartupTimeout(Duration.of(120, SECONDS));

        container.start();

        if (config.enableDebugging) {
            LOG.info("Container debug port: " + container.getMappedPort(DEBUG_PORT));
        }
        return container;
    }

    private static Path pathTo(String propertyName) {
        return Paths.get(property(propertyName));
    }

    private static String property(String propertyName) {
        return PropertyLoader.get("api-it-tests.properties", propertyName);
    }
}
