/*
 * */
package com.synectiks.process.common.testing.graylognode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class ExecutableFileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutableFileUtil.class);

    static void makeSureExecutableIsFound(String executableName) {
        String path = System.getenv("PATH");

        File executableFile = Arrays.stream(path.split(File.pathSeparator))
                .map(d -> new File(d, executableName))
                .filter(f -> f.isFile() && f.canExecute())
                .findFirst()
                .orElseThrow(() -> executableNotFoundException(executableName, path));

        LOG.info("Found executable {} at {}", executableName, executableFile.getAbsolutePath());
    }

    private static RuntimeException executableNotFoundException(String executableName, String path) {
        String msg = String.format(Locale.US, "Could not find executable %s in PATH [%s]", executableName, path);
        if (path.contains("~")) {
            msg += "\nAt least one ~ character was found in your path. " +
                    "Since tilde-expansion doesn't work in most implementations of /bin/sh, " +
                    "please make sure that the path to your executable doesn't contain one.";
        }
        return new RuntimeException(msg);
    }
}
