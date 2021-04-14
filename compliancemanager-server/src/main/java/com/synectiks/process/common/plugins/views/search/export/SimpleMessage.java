/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.google.auto.value.AutoValue;

import java.util.LinkedHashMap;

@AutoValue
public abstract class SimpleMessage {
    public static SimpleMessage from(String index, LinkedHashMap<String, Object> fieldsMap) {
        return builder().fields(fieldsMap).index(index).build();
    }

    public abstract LinkedHashMap<String, Object> fields();

    public abstract String index();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    public Object valueFor(String fieldName) {
        return fields().get(fieldName);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder fields(LinkedHashMap<String, Object> fields);

        public abstract Builder index(String index);

        public static Builder create() {
            return new AutoValue_SimpleMessage.Builder();
        }

        abstract SimpleMessage autoBuild();

        public SimpleMessage build() {
            return autoBuild();
        }
    }
}
