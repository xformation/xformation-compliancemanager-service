/*
 * */
package com.synectiks.process.common.testing.elasticsearch;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Checks if a test method is using the {@link SkipDefaultIndexTemplate} annotation and exposes that information
 * with the {@link #shouldSkip()} method.
 */
public class SkipDefaultIndexTemplateWatcher extends TestWatcher {
    private boolean skipIndexTemplateCreation = false;

    @Override
    protected void starting(Description description) {
        final SkipDefaultIndexTemplate skip = description.getAnnotation(SkipDefaultIndexTemplate.class);
        this.skipIndexTemplateCreation = skip != null;
    }

    /**
     * Returns true when the currently executed test method has the {@link SkipDefaultIndexTemplate} annotation.
     *
     * @return true when the current test method has the annotation, false otherwise
     */
    public boolean shouldSkip() {
        return skipIndexTemplateCreation;
    }
}
