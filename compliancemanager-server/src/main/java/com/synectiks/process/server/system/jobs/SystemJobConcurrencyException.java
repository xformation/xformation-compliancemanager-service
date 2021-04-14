/*
 * */
package com.synectiks.process.server.system.jobs;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class SystemJobConcurrencyException extends Exception {

    public SystemJobConcurrencyException() {
        super();
    }

    public SystemJobConcurrencyException(String msg) {
        super(msg);
    }

}
