/*
 * */
package com.synectiks.process.common.schema;

/**
 * Field names used in the standard alertmanager Schema.
 *
 * @deprecated Please use the appropriate enums in this package rather than this collection of strings
 */
@Deprecated
public class GraylogSchemaFields {

    public static final String FIELD_TIMESTAMP = "timestamp";

    public static final String FIELD_ILLUMINATE_EVENT_CATEGORY = "xfalert_event_category";
    public static final String FIELD_ILLUMINATE_EVENT_SUBCATEGORY = "xfalert_event_subcategory";
    public static final String FIELD_ILLUMINATE_EVENT_TYPE = "xfalert_event_type";
    public static final String FIELD_ILLUMINATE_EVENT_TYPE_CODE = "xfalert_event_type_code";
    public static final String FIELD_ILLUMINATE_TAGS = "xfalert_tags";
}
