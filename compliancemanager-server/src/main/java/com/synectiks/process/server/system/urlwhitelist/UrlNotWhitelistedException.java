/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

/**
 * Indicates that there was an attempt to access a URL which is not whitelisted.
 */
public class UrlNotWhitelistedException extends Exception {

    /**
     * Create an exception with a message stating that the given URL is not whitelisted.
     */
    public static UrlNotWhitelistedException forUrl(String url) {
        return new UrlNotWhitelistedException("URL <" + url + "> is not whitelisted.");
    }

    public UrlNotWhitelistedException(String message) {
        super(message);
    }
}
