/*
 * */
package com.synectiks.process.server.contentpacks.constraints;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.contentpacks.constraints.GraylogVersionConstraintChecker;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;
import com.synectiks.process.server.contentpacks.model.constraints.ConstraintCheckResult;
import com.synectiks.process.server.contentpacks.model.constraints.GraylogVersionConstraint;
import com.synectiks.process.server.contentpacks.model.constraints.PluginVersionConstraint;

import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GraylogVersionConstraintCheckerTest {
    @Test
    public void checkConstraints() {
        final GraylogVersionConstraintChecker constraintChecker = new GraylogVersionConstraintChecker("1.0.0");

        final GraylogVersionConstraint graylogVersionConstraint = GraylogVersionConstraint.builder()
                .version("^1.0.0")
                .build();
        final PluginVersionConstraint pluginVersionConstraint = PluginVersionConstraint.builder()
                .pluginId("unique-id")
                .version("^1.0.0")
                .build();
        final ImmutableSet<Constraint> requiredConstraints = ImmutableSet.of(graylogVersionConstraint, pluginVersionConstraint);
        final Set<ConstraintCheckResult> result = constraintChecker.checkConstraints(requiredConstraints);
        assertThat(result.stream().allMatch(c -> c.fulfilled())).isTrue();
    }

    @Test
    public void checkConstraintsFails() {
        final GraylogVersionConstraintChecker constraintChecker = new GraylogVersionConstraintChecker("1.0.0");

        final GraylogVersionConstraint graylogVersionConstraint = GraylogVersionConstraint.builder()
                .version("^2.0.0")
                .build();
        final PluginVersionConstraint pluginVersionConstraint = PluginVersionConstraint.builder()
                .pluginId("unique-id")
                .version("^1.0.0")
                .build();
        final ImmutableSet<Constraint> requiredConstraints = ImmutableSet.of(graylogVersionConstraint, pluginVersionConstraint);
        final Set<ConstraintCheckResult> result = constraintChecker.checkConstraints(requiredConstraints);
        assertThat(result.stream().allMatch(c -> !c.fulfilled())).isTrue();
    }
}