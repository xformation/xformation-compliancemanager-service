/*
 * */
package com.synectiks.process.server.jackson;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

public interface TypeReferences {
    TypeReference<Map<Object, Object>> MAP_OBJECT_OBJECT = new TypeReference<Map<Object, Object>>() {
    };
    TypeReference<Map<String, Object>> MAP_STRING_OBJECT = new TypeReference<Map<String, Object>>() {
    };
    TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {
    };
}