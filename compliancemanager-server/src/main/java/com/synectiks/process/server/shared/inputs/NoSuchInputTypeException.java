/*
 * */
package com.synectiks.process.server.shared.inputs;

public class NoSuchInputTypeException extends Exception {

    public NoSuchInputTypeException() {
        super();
    }

    public NoSuchInputTypeException(String s) {
        super(s);
    }

    public NoSuchInputTypeException(String s, Throwable e) {
        super(s, e);
    }

}
