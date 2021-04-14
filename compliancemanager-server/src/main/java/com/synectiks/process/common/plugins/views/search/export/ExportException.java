/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

public class ExportException extends RuntimeException {
    public ExportException(String msg) {
        super(msg);
    }

    public ExportException(String msg, Exception cause) {
        super(msg, cause);
    }
}
