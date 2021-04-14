/*
 * */
package com.synectiks.process.server.contentpacks.constraints;

import java.util.Collection;
import java.util.Set;

import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.ConstraintCheckResult;

public interface ConstraintChecker {
    Set<Constraint> ensureConstraints(Collection<Constraint> requestedConstraints);
    Set<ConstraintCheckResult> checkConstraints(Collection<Constraint> requestedConstraints);
}
