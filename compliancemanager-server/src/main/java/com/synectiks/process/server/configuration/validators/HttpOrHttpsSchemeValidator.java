/*
 * */
package com.synectiks.process.server.configuration.validators;

import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HttpOrHttpsSchemeValidator implements Validator<String> {

    private static final List<String> validScheme = Arrays.asList("http", "https");

    @Override
    public void validate(String name, String value) throws ValidationException {
        if (!validScheme.contains(value.toLowerCase(Locale.ENGLISH))) {
            throw new ValidationException(String.format(Locale.ENGLISH, "Parameter " + name + " must be one of [%s]", String.join(",", validScheme)));
        }
    }
}
