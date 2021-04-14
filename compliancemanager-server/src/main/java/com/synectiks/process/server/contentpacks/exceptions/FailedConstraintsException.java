/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

import java.util.Collection;

import com.synectiks.process.server.contentpacks.model.constraints.Constraint;

public class FailedConstraintsException extends ContentPackException {
    private final Collection<Constraint> failedConstraints;

    public FailedConstraintsException(Collection<Constraint> failedConstraints) {
        super("Failed constraints: " + failedConstraints);
        this.failedConstraints = failedConstraints;
    }

    public Collection<Constraint> getFailedConstraints() {
        return failedConstraints;
    }
}
