/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoValueSubtypeResolver extends StdSubtypeResolver {
    @Override
    protected void _collectAndResolve(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, AnnotationIntrospector ai, HashMap<NamedType, NamedType> collectedSubtypes) {
        super._collectAndResolve(annotatedType, resolveAutoValue(namedType), config, ai, collectedSubtypes);
    }

    @Override
    protected void _collectAndResolveByTypeId(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, Set<Class<?>> typesHandled, Map<String, NamedType> byName) {
        super._collectAndResolveByTypeId(annotatedType, resolveAutoValue(namedType), config, typesHandled, byName);
    }

    private NamedType resolveAutoValue(NamedType namedType) {
        final Class<?> cls = namedType.getType();
        if (cls.getSimpleName().startsWith("AutoValue_")) {
            return new NamedType(cls.getSuperclass(), namedType.getName());
        } else {
            return namedType;
        }
    }
}
