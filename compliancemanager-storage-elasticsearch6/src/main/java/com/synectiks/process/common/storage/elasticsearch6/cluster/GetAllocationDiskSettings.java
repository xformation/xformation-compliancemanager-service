/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.cluster;

import io.searchbox.cluster.GetSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GetAllocationDiskSettings extends GetSettings {

    protected GetAllocationDiskSettings(Builder builder) {
        super(builder);
    }

    public static class Builder extends GetSettings.Builder {
        public Builder() {
            this.configureIncludeDefaults();
            this.configureSettingsFilter();
        }

        private void configureIncludeDefaults() {
            this.parameters.put("include_defaults", true);
        }

        private void configureSettingsFilter() {
            this.parameters.put("filter_path", this.getFilterPathValue());
        }

        private String getFilterPathValue() {
            List<String> filterPaths = new ArrayList<>();
            String commonFilterPath = "cluster.routing.allocation.disk";
            List<String> settingsGroup = Arrays.asList("defaults", "persistent", "transient");
            for (String settingGroup: settingsGroup) {
                filterPaths.add(String.join(".", settingGroup, commonFilterPath));
            }
            return String.join(",", filterPaths);
        }

        public GetAllocationDiskSettings build() {
            return new GetAllocationDiskSettings(this);
        }
    }
}
