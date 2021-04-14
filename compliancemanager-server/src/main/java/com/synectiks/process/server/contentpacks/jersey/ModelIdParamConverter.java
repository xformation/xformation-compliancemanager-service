/*
 * */
package com.synectiks.process.server.contentpacks.jersey;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import com.synectiks.process.server.contentpacks.model.ModelId;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Singleton
public class ModelIdParamConverter implements ParamConverter<ModelId> {
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelId fromString(final String value) {
        return ModelId.of(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(final ModelId value) {
        return value == null ? null : value.id();
    }

    public static class Provider implements ParamConverterProvider {
        private final ModelIdParamConverter paramConverter = new ModelIdParamConverter();

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        @Nullable
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType,
                                                  final Annotation[] annotations) {
            return ModelId.class.isAssignableFrom(rawType) ? (ParamConverter<T>) paramConverter : null;
        }
    }
}