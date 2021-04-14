/*
 * */
package com.synectiks.process.server.contentpacks.exceptions;

import java.util.Collection;

public class MissingParametersException extends ContentPackException {
    private final Collection<String> missingParameters;

    public MissingParametersException(Collection<String> missingParameters) {
        super("Missing parameters: " + missingParameters);
        this.missingParameters = missingParameters;
    }

    public Collection<String> getMissingParameters() {
        return missingParameters;
    }
}
