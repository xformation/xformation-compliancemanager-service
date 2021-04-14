/*
 * */
package com.synectiks.process.server.migrations;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

public abstract class Migration implements Comparable<Migration> {
    private static final Comparator<Migration> COMPARATOR = Comparator.comparingLong(migration -> migration.createdAt().toEpochSecond());

    public abstract ZonedDateTime createdAt();

    public abstract void upgrade();

    @Override
    public int compareTo(Migration that) {
        return COMPARATOR.compare(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(COMPARATOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Migration that = (Migration) o;
        return Objects.equals(this.createdAt(), that.createdAt());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' + createdAt().format(DateTimeFormatter.ISO_DATE_TIME) + '}';
    }
}
