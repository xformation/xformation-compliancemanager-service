/*
 * */
package com.synectiks.process.server.cluster;

import com.synectiks.process.server.database.NotFoundException;

public class NodeNotFoundException extends NotFoundException {
    public NodeNotFoundException(String msg) {
        super(msg);
    }
}
