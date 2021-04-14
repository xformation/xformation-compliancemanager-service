/*
 * */
package com.synectiks.process.server.indexer.searches;

import java.util.Locale;

import com.synectiks.process.server.plugin.Message;

public class Sorting {

    public static final Sorting DEFAULT = new Sorting(Message.FIELD_TIMESTAMP, Direction.DESC);

    public enum Direction {
        ASC,
        DESC
    }

    private final String field;
    private final Direction direction;

    public Sorting(String field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public Direction getDirection() { return this.direction; }

    public static Sorting fromApiParam(String param) {
        if (param == null || !param.contains(":")) {
            throw new IllegalArgumentException("Invalid sorting parameter: " + param);
        }

        String[] parts = param.split(":");

        return new Sorting(parts[0], Direction.valueOf(parts[1].toUpperCase(Locale.ENGLISH)));
    }

}
