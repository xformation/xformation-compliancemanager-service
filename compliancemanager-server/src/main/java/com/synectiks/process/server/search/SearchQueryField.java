/*
 * */
package com.synectiks.process.server.search;

public class SearchQueryField {
    public enum Type {
        STRING, DATE, INT, LONG;
    }

    private final String dbField;
    private final Type fieldType;

    public static SearchQueryField create(String dbField) {
        return new SearchQueryField(dbField, Type.STRING);
    }

    public static SearchQueryField create(String dbField, Type fieldType) {
        return new SearchQueryField(dbField, fieldType);
    }

    public SearchQueryField(String dbField, Type fieldType) {
        this.dbField = dbField;
        this.fieldType = fieldType;
    }

    public String getDbField() {
        return dbField;
    }

    public Type getFieldType() {
        return fieldType;
    }
}
