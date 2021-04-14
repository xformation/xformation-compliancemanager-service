/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

public class ContentPackException extends RuntimeException {
    public ContentPackException() {
        super();
    }

    public ContentPackException(String message) {
        super(message);
    }

    public ContentPackException(String message, Throwable cause) {
        super(message, cause);
    }
}
