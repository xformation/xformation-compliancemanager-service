/*
 * */
package com.synectiks.process.common.plugins.netflow.flows;

public class CorruptFlowPacketException extends FlowException {
    public CorruptFlowPacketException() {
        super();
    }

    public CorruptFlowPacketException(String message) {
        super(message);
    }
}
