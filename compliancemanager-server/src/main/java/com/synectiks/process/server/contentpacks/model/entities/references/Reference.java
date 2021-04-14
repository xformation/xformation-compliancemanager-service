/*
 * */
package com.synectiks.process.server.contentpacks.model.entities.references;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.synectiks.process.server.contentpacks.jackson.ReferenceConverter;

@JsonDeserialize(converter = ReferenceConverter.class)
public interface Reference {
}
