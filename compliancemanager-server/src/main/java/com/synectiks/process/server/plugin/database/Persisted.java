/*
 * */
package com.synectiks.process.server.plugin.database;

import java.util.Map;

import com.synectiks.process.server.plugin.database.validators.Validator;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface Persisted {
    String getId();

    Map<String, Object> getFields();
    Map<String, Validator> getValidations();
    Map<String, Validator> getEmbeddedValidations(String key);

    Map<String, Object> asMap();
}
