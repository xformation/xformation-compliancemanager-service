/*
 * */
package com.synectiks.process.server.plugin.indexer.searches.timeranges;

public class InvalidRangeParametersException extends Exception {
    public InvalidRangeParametersException() {
        super();
    }

    public InvalidRangeParametersException(String msg) {
        super(msg);
    }

    public InvalidRangeParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}

