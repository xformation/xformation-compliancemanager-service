/*
 * */
package com.synectiks.process.common.plugins.netflow.flows;

public class InvalidFlowVersionException extends FlowException {
    public InvalidFlowVersionException(int version) {
        super("Invalid NetFlow version " + version);
    }
}
