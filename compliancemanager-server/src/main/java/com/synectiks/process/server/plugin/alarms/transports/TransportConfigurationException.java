/*
 * */
package com.synectiks.process.server.plugin.alarms.transports;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class TransportConfigurationException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -851955448143684632L;

	public TransportConfigurationException() {
        super();
    }
    
    public TransportConfigurationException(String msg) {
        super(msg);
    }
    
}
