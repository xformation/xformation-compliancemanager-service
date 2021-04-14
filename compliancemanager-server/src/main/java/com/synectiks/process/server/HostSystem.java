/*
 * */
package com.synectiks.process.server;

/**
 * Utility class that provides access to host system information.
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class HostSystem  {

    /**
     * @return total number of processors or cores available to the JVM
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}