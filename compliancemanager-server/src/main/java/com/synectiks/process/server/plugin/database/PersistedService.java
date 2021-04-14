/*
 * */
package com.synectiks.process.server.plugin.database;

import javax.annotation.Nullable;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

import java.util.List;
import java.util.Map;

public interface PersistedService {
    <T extends Persisted> int destroy(T model);

    <T extends Persisted> int destroyAll(Class<T> modelClass);

    <T extends Persisted> String save(T model) throws ValidationException;

    @Nullable
    <T extends Persisted> String saveWithoutValidation(T model);

    <T extends Persisted> Map<String, List<ValidationResult>> validate(T model, Map<String, Object> fields);

    <T extends Persisted> Map<String, List<ValidationResult>> validate(T model);

    Map<String, List<ValidationResult>> validate(Map<String, Validator> validators, Map<String, Object> fields);
}
