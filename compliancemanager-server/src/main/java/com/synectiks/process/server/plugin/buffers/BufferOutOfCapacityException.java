/*
 * */
package com.synectiks.process.server.plugin.buffers;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class BufferOutOfCapacityException extends Exception {
    
    /**
	 * Re-generate if you modify the class structure.
	 */
	private static final long serialVersionUID = -2497994875518554556L;

	public BufferOutOfCapacityException() {
        super();
    }
    
    public BufferOutOfCapacityException(String msg) {
        super(msg);
    }
    
}
