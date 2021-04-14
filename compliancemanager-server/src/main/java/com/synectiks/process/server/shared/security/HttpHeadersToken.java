/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authc.HostAuthenticationToken;

import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

public class HttpHeadersToken implements HostAuthenticationToken {

    private final MultivaluedMap<String, String> httpHeaders;
    private final String host;
    private final String remoteAddr;

    public HttpHeadersToken(MultivaluedMap<String, String> httpHeaders, String host, String remoteAddr) {
        this.httpHeaders = httpHeaders;
        this.host = host;
        this.remoteAddr = remoteAddr;
    }

    /**
     * A HttpHeadersToken does not have a natural principal associated with it, so this is always null.
     *
     * @return null
     */
    @Override
    @Nullable
    public Object getPrincipal() {
        return null;
    }

    /**
     * A HttpHeadersToken does not have a natural credential associated with it, so this is always null.
     *
     * @return null
     */
    @Override
    @Nullable
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getHost() {
        return host;
    }

    public MultivaluedMap<String, String> getHeaders() {
        return httpHeaders;
    }

    /**
     * The direct remote address, if the request came through a proxy, this will be the address of last hop.
     * Typically used to verify that a client is "trusted".
     * @return the direct peer's address
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }
}
