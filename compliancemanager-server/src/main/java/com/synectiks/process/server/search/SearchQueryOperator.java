/*
 * */
package com.synectiks.process.server.search;

import org.mongojack.DBQuery;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public abstract class SearchQueryOperator {
    public abstract DBQuery.Query buildQuery(String key, Object value);

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass());
    }

    public static class Equals extends SearchQueryOperator {
        @Override
        public DBQuery.Query buildQuery(String key, Object value) {
            return DBQuery.is(key, value);
        }
    }

    public static class Regexp extends SearchQueryOperator {
        @Override
        public DBQuery.Query buildQuery(String key, Object value) {
            return DBQuery.regex(key, Pattern.compile(Pattern.quote(value.toString()), CASE_INSENSITIVE));
        }
    }

    public static class Greater extends SearchQueryOperator {
        @Override
        public DBQuery.Query buildQuery(String key, Object value) {
            return DBQuery.greaterThan(key, value);
        }
    }

    public static class GreaterEquals extends SearchQueryOperator {
        @Override
        public DBQuery.Query buildQuery(String key, Object value) {
            return DBQuery.greaterThanEquals(key, value);
        }
    }

    public static class Less extends SearchQueryOperator {
        @Override
        public DBQuery.Query buildQuery(String key, Object value) {
            return DBQuery.lessThan(key, value);
        }
    }

    public static class LessEquals extends SearchQueryOperator {
        @Override
        public DBQuery.Query buildQuery(String key, Object value) {
            return DBQuery.lessThanEquals(key, value);
        }
    }
}
