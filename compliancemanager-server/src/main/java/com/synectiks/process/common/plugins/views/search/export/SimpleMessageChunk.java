/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.google.auto.value.AutoValue;

import static com.synectiks.process.common.plugins.views.search.export.LinkedHashSetUtil.linkedHashSetOf;

import java.util.LinkedHashSet;

@AutoValue
public abstract class SimpleMessageChunk {
    public static SimpleMessageChunk from(LinkedHashSet<String> fieldsInOrder, LinkedHashSet<SimpleMessage> messages) {
        return builder().fieldsInOrder(fieldsInOrder).messages(messages).build();
    }

    public static SimpleMessageChunk from(LinkedHashSet<String> fieldsInOrder, SimpleMessage... messages) {
        return from(fieldsInOrder, linkedHashSetOf(messages));
    }

    public abstract LinkedHashSet<String> fieldsInOrder();

    public abstract LinkedHashSet<SimpleMessage> messages();

    public abstract boolean isFirstChunk();

    public int size() {
        return messages().size();
    }

    public static Builder builder() {
        return Builder.create().isFirstChunk(false);
    }

    public abstract Builder toBuilder();

    public Object[][] getAllValuesInOrder() {
        return messages().stream()
                .map(this::valuesFrom)
                .toArray(Object[][]::new);
    }

    private Object[] valuesFrom(SimpleMessage simpleMessage) {
        return fieldsInOrder().stream().map(simpleMessage::valueFor).toArray();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder fieldsInOrder(LinkedHashSet<String> fieldsInOrder);

        public abstract Builder messages(LinkedHashSet<SimpleMessage> messages);

        public abstract Builder isFirstChunk(boolean isFirstChunk);

        public static Builder create() {
            return new AutoValue_SimpleMessageChunk.Builder();
        }

        abstract SimpleMessageChunk autoBuild();

        public SimpleMessageChunk build() {
            return autoBuild();
        }
    }
}
