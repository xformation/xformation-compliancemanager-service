/*
 * */
package com.synectiks.process.server.decorators;

import java.util.Map;
import java.util.Optional;

public interface Decorator {
    String id();
    String type();
    Optional<String> stream();
    Map<String, Object> config();
    int order();
}
