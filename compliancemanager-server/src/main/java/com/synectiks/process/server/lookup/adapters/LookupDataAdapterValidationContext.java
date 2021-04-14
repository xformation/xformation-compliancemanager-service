/*
 * */
package com.synectiks.process.server.lookup.adapters;

import com.google.inject.Inject;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;

/**
 * Context object for configurations which require access to services to perform validation.
 */
public class LookupDataAdapterValidationContext {
    private final UrlWhitelistService urlWhitelistService;

    @Inject
    public LookupDataAdapterValidationContext(UrlWhitelistService urlWhitelistService) {
        this.urlWhitelistService = urlWhitelistService;
    }

    public UrlWhitelistService getUrlWhitelistService() {
        return urlWhitelistService;
    }
}
