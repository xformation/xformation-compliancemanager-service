/*
 * */
package com.synectiks.process.server.plugin.database.validators;

import com.google.common.base.MoreObjects;

public abstract class ValidationResult {
    public abstract boolean passed();

    public static class ValidationPassed extends ValidationResult {
        @Override
        public boolean passed() {
            return true;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("passed", passed())
                .toString();
        }
    }

    public static class ValidationFailed extends ValidationResult {
        private final String error;

        public ValidationFailed(String errors) {
            this.error = errors;
        }

        public String getError() {
            return error;
        }

        @Override
        public boolean passed() {
            return false;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("passed", passed())
                .add("error", getError())
                .toString();
        }
    }
}
