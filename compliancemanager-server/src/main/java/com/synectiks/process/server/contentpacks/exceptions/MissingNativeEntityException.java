/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;

public class MissingNativeEntityException extends ContentPackException {
    private final EntityDescriptor missingEntityDescriptor;

    public MissingNativeEntityException(EntityDescriptor missingEntityDescriptor) {
        super("Missing native entity: " + missingEntityDescriptor);
        this.missingEntityDescriptor = missingEntityDescriptor;
    }

    public EntityDescriptor getMissingEntityDescriptor() {
        return missingEntityDescriptor;
    }
}
