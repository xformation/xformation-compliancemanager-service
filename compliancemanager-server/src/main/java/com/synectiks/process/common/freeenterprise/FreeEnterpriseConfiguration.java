/*
 * */
package com.synectiks.process.common.freeenterprise;

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.validators.URIAbsoluteValidator;
import com.synectiks.process.server.plugin.PluginConfigBean;

import java.net.URI;

public class FreeEnterpriseConfiguration implements PluginConfigBean {
    private static final String PREFIX = "free_enterprise_";

    public static final String SERVICE_URL = PREFIX + "service_url";

    @Parameter(value = SERVICE_URL, validators = URIAbsoluteValidator.class)
    private URI serviceUrl = URI.create("https://api.alertmanager.com/");

    public URI getServiceUrl() {
        return serviceUrl;
    }
}
