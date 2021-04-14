/*
 * */
package com.synectiks.process.server.database;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class NotFoundException extends Exception {

	private static final long serialVersionUID = 3628114644247976589L;

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
