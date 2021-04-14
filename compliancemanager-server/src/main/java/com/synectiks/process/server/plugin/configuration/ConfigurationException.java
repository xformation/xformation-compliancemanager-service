/*
 * */
package com.synectiks.process.server.plugin.configuration;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class ConfigurationException extends Exception {
    
    /**
	 * Re-generate if you modify the class structure.
	 */
	private static final long serialVersionUID = 8745066310811183675L;

	public ConfigurationException() {
        super();
    }
    
    public ConfigurationException(String msg) {
        super(msg);
    }

    public ConfigurationException(String msg, Throwable e) {
        super(msg, e);
    }
    
}