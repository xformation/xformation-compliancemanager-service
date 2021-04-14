/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

import java.util.Collection;

public class InvalidParametersException extends ContentPackException {
    private final Collection<String> invalidParameters;

    public InvalidParametersException(Collection<String> invalidParameters) {
        super("Invalid parameters: " + invalidParameters);
        this.invalidParameters = invalidParameters;
    }

    public Collection<String> getInvalidParameters() {
        return invalidParameters;
    }
}
