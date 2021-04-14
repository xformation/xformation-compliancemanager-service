/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParameterExpansionError implements SearchError {
    private final String parameterName;
    private final String description;

    public ParameterExpansionError(String name) {
        this.parameterName = name;
        this.description = "Error while expanding parameter <" + parameterName + ">";
    }

    public ParameterExpansionError(String name, String msg) {
        this.parameterName = name;
        this.description = "Error while expanding parameter <" + parameterName + ">: " + msg;
    }

    @JsonProperty("parameter")
    public String parameterName() {
        return parameterName;
    }

    @Override
    public String description() {
        return description;
    }
}
