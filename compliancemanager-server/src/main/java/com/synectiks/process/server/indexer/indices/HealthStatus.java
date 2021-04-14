/*
 * */
package com.synectiks.process.server.indexer.indices;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public enum HealthStatus {
    Red,
    Yellow,
    Green;

    public static HealthStatus fromString(String value) {
        checkNotNull(value);
        final String normalizedValue = value.toUpperCase(Locale.ENGLISH);
        switch (normalizedValue) {
            case "RED": return Red;
            case "YELLOW": return Yellow;
            case "GREEN": return Green;

            default: throw new IllegalArgumentException("Unable to parse health status from string (known: GREEN/YELLOW/RED): " + normalizedValue);
        }
    }
}
