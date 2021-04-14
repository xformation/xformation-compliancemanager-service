/*
 * */
package com.synectiks.process.server.configuration.validators;

import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.Validator;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.plugin.Version;

import java.util.List;

public class ElasticsearchVersionValidator implements Validator<Version> {
    private static final List<Version> SUPPORTED_ES_VERSIONS = ImmutableList.of(
            Version.from(6, 0, 0),
            Version.from(7, 0, 0)
    );

    @Override
    public void validate(String name, Version value) throws ValidationException {
        if (!SUPPORTED_ES_VERSIONS.contains(value)) {
            throw new ValidationException("Invalid Elasticsearch version specified in " + name + ": " + value
                    + ". Supported versions: " + SUPPORTED_ES_VERSIONS);
        }
    }
}
