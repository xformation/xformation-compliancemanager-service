/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

import java.util.Collection;

import com.synectiks.process.server.contentpacks.model.entities.Entity;

public class UnexpectedEntitiesException extends ContentPackException {
    private final Collection<Entity> unexpectedEntities;

    public UnexpectedEntitiesException(Collection<Entity> unexpectedEntities) {
        super("Unexpected entities in content pack: " + unexpectedEntities);
        this.unexpectedEntities = unexpectedEntities;
    }

    public Collection<Entity> getUnexpectedEntities() {
        return unexpectedEntities;
    }
}
