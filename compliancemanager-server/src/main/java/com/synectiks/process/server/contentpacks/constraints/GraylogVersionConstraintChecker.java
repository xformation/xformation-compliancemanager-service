/*
 * */
package com.synectiks.process.server.contentpacks.constraints;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.ConstraintCheckResult;
import com.synectiks.process.server.contentpacks.model.constraints.GraylogVersionConstraint;
import com.synectiks.process.server.semver4j.Requirement;
import com.synectiks.process.server.semver4j.Semver;

import java.util.Collection;
import java.util.Set;

public class GraylogVersionConstraintChecker implements ConstraintChecker {
    private final Semver serverVersion;

    public GraylogVersionConstraintChecker() {
        this(com.synectiks.process.server.plugin.Version.CURRENT_CLASSPATH.toString());
    }

    @VisibleForTesting
    GraylogVersionConstraintChecker(String serverVersion) {
        this(new Semver(serverVersion));
    }

    @VisibleForTesting
    GraylogVersionConstraintChecker(Semver serverVersion) {
        this.serverVersion = serverVersion;
    }


    @Override
    public Set<Constraint> ensureConstraints(Collection<Constraint> requestedConstraints) {
        final ImmutableSet.Builder<Constraint> fulfilledConstraints = ImmutableSet.builder();
        for (Constraint constraint : requestedConstraints) {
            if (constraint instanceof GraylogVersionConstraint) {
                final GraylogVersionConstraint versionConstraint = (GraylogVersionConstraint) constraint;
                final Requirement requiredVersion = versionConstraint.version();
                if (requiredVersion.isSatisfiedBy(serverVersion.withClearedSuffixAndBuild())) {
                    fulfilledConstraints.add(constraint);
                }
            }
        }
        return fulfilledConstraints.build();
    }

    @Override
    public Set<ConstraintCheckResult> checkConstraints(Collection<Constraint> requestedConstraints) {
        final ImmutableSet.Builder<ConstraintCheckResult> fulfilledConstraints = ImmutableSet.builder();
        for (Constraint constraint : requestedConstraints) {
            if (constraint instanceof GraylogVersionConstraint) {
                final GraylogVersionConstraint versionConstraint = (GraylogVersionConstraint) constraint;
                final Requirement requiredVersion = versionConstraint.version();
                final ConstraintCheckResult constraintCheckResult = ConstraintCheckResult.create(versionConstraint,
                        requiredVersion.isSatisfiedBy(serverVersion.withClearedSuffixAndBuild()));
                fulfilledConstraints.add(constraintCheckResult);
            }
        }
        return fulfilledConstraints.build();
    }
}
