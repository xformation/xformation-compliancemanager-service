/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import java.util.Map;

import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;

public class MissingCapabilitiesException extends RuntimeException {

    private final Map<String, PluginMetadataSummary> missingRequirements;

    public MissingCapabilitiesException(Map<String, PluginMetadataSummary> missingRequirements) {
        super();
        this.missingRequirements = missingRequirements;
    }

    public Map<String, PluginMetadataSummary> getMissingRequirements() {
        return missingRequirements;
    }
}
