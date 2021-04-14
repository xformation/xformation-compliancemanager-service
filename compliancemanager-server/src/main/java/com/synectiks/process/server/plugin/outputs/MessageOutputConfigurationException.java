/*
 * */
package com.synectiks.process.server.plugin.outputs;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class MessageOutputConfigurationException extends Exception {
    
    /**
	 * Re-generate if you modify the class structure.
	 */
	private static final long serialVersionUID = -4252325712098060658L;

	public MessageOutputConfigurationException() {
        super();
    }
    
    public MessageOutputConfigurationException(String msg) {
        super(msg);
    }
    
}
