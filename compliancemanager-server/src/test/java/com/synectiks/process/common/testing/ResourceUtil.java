/*
 * */
package com.synectiks.process.common.testing;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceUtil {
    public static File resourceToTmpFile(String resourceName) {
        final URL resource = ResourceUtil.class.getClassLoader().getResource(resourceName);

        if (resource == null) {
            throw new RuntimeException("Couldn't load resource " + resourceName);
        }

        return resourceURLToTmpFile(resource).toFile();
    }

    public static Path resourceURLToTmpFile(URL resourceUrl) {
        final Path path = createTempFile(resourceUrl);

        try {
            FileUtils.copyInputStreamToFile(resourceUrl.openStream(), path.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Error copying resource to file: " + resourceUrl, e);
        }

        return path;
    }

    private static Path createTempFile(URL resourceUrl) {
        try {
            final Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
            final Path path = Files.createTempFile(tmpDir, "graylog-test-resource-file-", null);

            // Temp files should automatically be deleted on exit of the JVM
            path.toFile().deleteOnExit();

            return path;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create temp resource file: " + resourceUrl.toString(), e);
        }
    }
}
