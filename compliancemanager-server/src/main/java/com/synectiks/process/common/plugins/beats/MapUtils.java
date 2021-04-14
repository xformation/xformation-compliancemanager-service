/*
 * */
package com.synectiks.process.common.plugins.beats;

import java.util.HashMap;
import java.util.Map;

public final class MapUtils {
    public static Map<String, Object> flatten(Map<String, Object> originalMap, String parentKey, String separator) {
        final Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            final String key = parentKey.isEmpty() ? entry.getKey() : parentKey + separator + entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> valueMap = (Map<String, Object>) value;
                result.putAll(flatten(valueMap, key, separator));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    public static void renameKey(Map<String, Object> map, String originalKey, String newKey) {
        if (map.containsKey(originalKey)) {
            final Object value = map.remove(originalKey);
            map.put(newKey, value);
        }
    }

    public static Map<String, Object> replaceKeyCharacter(Map<String, Object> map, char oldChar, char newChar) {
        final Map<String, Object> result = new HashMap<>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            final String key = entry.getKey().replace(oldChar, newChar);
            final Object value = entry.getValue();
            result.put(key, value);
        }
        return result;
    }

    private MapUtils() {
        throw new AssertionError("No instances allowed");
    }
}
