/*
 * */
package com.synectiks.process.server.plugin;

public class ProcessingPauseLockedException extends RuntimeException {

    public ProcessingPauseLockedException() {
        super();
    }

    public ProcessingPauseLockedException(String msg) {
        super(msg);
    }

}
