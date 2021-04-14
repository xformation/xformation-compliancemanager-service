/*
 * */
package com.synectiks.process.server.plugin.alarms.callbacks;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class AlarmCallbackException extends Exception {
    
    /**
	 * Re-generate if you modify the class structure.
	 */
	private static final long serialVersionUID = 8249565372019139524L;

	public AlarmCallbackException() {
        super();
    }
    
    public AlarmCallbackException(String msg) {
        super(msg);
    }

    public AlarmCallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
