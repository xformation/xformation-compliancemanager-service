/*
 * */
package com.synectiks.process.server.contentpacks.model.entities.references;

import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.Map;

public class ReferenceMap extends ForwardingMap<String, Reference> implements Reference {
    private final Map<String, Reference> map;

    public ReferenceMap(Map<String, Reference> map) {
        this.map = map;
    }

    public ReferenceMap() {
        this(new HashMap<>());
    }

    @Override
    protected Map<String, Reference> delegate() {
        return map;
    }
}
