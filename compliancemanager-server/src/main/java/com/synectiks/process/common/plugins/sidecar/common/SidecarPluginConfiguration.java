/*
 * */
package com.synectiks.process.common.plugins.sidecar.common;

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.util.Duration;
import com.github.joschi.jadconfig.validators.PositiveDurationValidator;
import com.github.joschi.jadconfig.validators.PositiveIntegerValidator;
import com.github.joschi.jadconfig.validators.StringNotEmptyValidator;
import com.synectiks.process.server.plugin.PluginConfigBean;

public class SidecarPluginConfiguration implements PluginConfigBean {
    private static final String PREFIX = "sidecar_";

    @Parameter(value = PREFIX + "user", validator = StringNotEmptyValidator.class)
    private String user = "alertmanager-sidecar";

    @Parameter(value = PREFIX + "cache_time", validator = PositiveDurationValidator.class)
    private Duration cacheTime = Duration.hours(1L);

    @Parameter(value = PREFIX + "cache_max_size", validator = PositiveIntegerValidator.class)
    private int cacheMaxSize = 100;

    public Duration getCacheTime() {
        return cacheTime;
    }

    public String getUser() {
        return user;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }
}
