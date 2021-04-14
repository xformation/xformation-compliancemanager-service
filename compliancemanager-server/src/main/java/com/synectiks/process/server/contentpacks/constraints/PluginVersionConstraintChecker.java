/*
 * */
package com.synectiks.process.server.contentpacks.constraints;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.ConstraintCheckResult;
import com.synectiks.process.server.contentpacks.model.constraints.PluginVersionConstraint;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.semver4j.Requirement;
import com.synectiks.process.server.semver4j.Semver;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class PluginVersionConstraintChecker implements ConstraintChecker {
    private final Set<Semver> pluginVersions;

    @Inject
    public PluginVersionConstraintChecker(Set<PluginMetaData> pluginMetaData) {
        pluginVersions = pluginMetaData.stream()
                .map(PluginMetaData::getVersion)
                .map(Version::toString)
                .map(Semver::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Constraint> ensureConstraints(Collection<Constraint> requestedConstraints) {
        final ImmutableSet.Builder<Constraint> fulfilledConstraints = ImmutableSet.builder();
        for (Constraint constraint : requestedConstraints) {
            if (constraint instanceof PluginVersionConstraint) {
                final PluginVersionConstraint versionConstraint = (PluginVersionConstraint) constraint;
                final Requirement requiredVersion = versionConstraint.version();

                for (Semver pluginVersion : pluginVersions) {
                    if (requiredVersion.isSatisfiedBy(pluginVersion)) {
                        fulfilledConstraints.add(constraint);
                    }
                }
            }
        }
        return fulfilledConstraints.build();
    }

    @Override
    public Set<ConstraintCheckResult> checkConstraints(Collection<Constraint> requestedConstraints) {
        final ImmutableSet.Builder<ConstraintCheckResult> fulfilledConstraints = ImmutableSet.builder();
        for (Constraint constraint : requestedConstraints) {
            if (constraint instanceof PluginVersionConstraint) {
                final PluginVersionConstraint versionConstraint = (PluginVersionConstraint) constraint;
                final Requirement requiredVersion = versionConstraint.version();

                boolean result = false;
                for (Semver pluginVersion : pluginVersions) {
                    if (requiredVersion.isSatisfiedBy(pluginVersion)) {
                        result = true;
                    }
                }
                ConstraintCheckResult constraintCheckResult = ConstraintCheckResult.create(versionConstraint, result);
                fulfilledConstraints.add(constraintCheckResult);
            }
        }
        return fulfilledConstraints.build();
    }
}
