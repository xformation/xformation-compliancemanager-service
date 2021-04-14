/*
 * */
package com.synectiks.process.common.events.fields.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.events.event.EventWithContext;
import com.synectiks.process.common.events.fields.FieldValue;

public abstract class AbstractFieldValueProvider implements FieldValueProvider {
    public interface Factory<TYPE extends FieldValueProvider> extends FieldValueProvider.Factory<TYPE> {
        @Override
        TYPE create(Config config);
    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFieldValueProvider.class);

    private final Config config;

    public AbstractFieldValueProvider(Config config) {
        this.config = config;
    }

    @Override
    public FieldValue get(String fieldName, EventWithContext eventWithContext) {
        try {
            return doGet(fieldName, eventWithContext);
        } catch (Exception e) {
            LOG.error("Couldn't execute field value provider: {}", config, e);
            return FieldValue.error();
        }
    }

    protected abstract FieldValue doGet(String fieldName, EventWithContext eventWithContext);
}
