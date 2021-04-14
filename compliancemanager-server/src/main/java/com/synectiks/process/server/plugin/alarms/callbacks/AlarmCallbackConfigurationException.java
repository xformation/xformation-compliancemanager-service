/*
 * */
package com.synectiks.process.server.plugin.alarms.callbacks;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class AlarmCallbackConfigurationException extends Exception {
    
    /**
	 * Re-generate if you modify the class structure.
	 */
	private static final long serialVersionUID = 1762085797851052304L;

	public AlarmCallbackConfigurationException() {
        super();
    }
    
    public AlarmCallbackConfigurationException(String msg) {
        super(msg);
    }
    
}
