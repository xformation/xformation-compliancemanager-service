/*
 * */
package com.synectiks.process.server.shared.security.tls;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public abstract class DefaultTLSProtocolProvider {
    // Defaults to TLS protocols that are currently considered secure
    public static final Set<String> DEFAULT_TLS_PROTOCOLS = ImmutableSet.of("TLSv1.2", "TLSv1.3");

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTLSProtocolProvider.class);
    private static Set<String> defaultSupportedTlsProtocols = null;

    public synchronized static Set<String> getDefaultSupportedTlsProtocols() {
        if (defaultSupportedTlsProtocols != null) {
            return defaultSupportedTlsProtocols;
        }

        final Set<String> tlsProtocols = Sets.newHashSet(DEFAULT_TLS_PROTOCOLS);
        try {
            final Set<String> supportedProtocols = ImmutableSet.copyOf(SSLContext.getDefault().createSSLEngine().getSupportedProtocols());
            if (tlsProtocols.retainAll(supportedProtocols)) {
                LOG.warn("JRE doesn't support all default TLS protocols. Changing <{}> to <{}>", DEFAULT_TLS_PROTOCOLS, tlsProtocols);
            }
            defaultSupportedTlsProtocols = tlsProtocols;
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Failed to detect supported TLS protocols. Keeping default <{}>", DEFAULT_TLS_PROTOCOLS, e);
            defaultSupportedTlsProtocols = DEFAULT_TLS_PROTOCOLS;
        }
        return defaultSupportedTlsProtocols;
    }
}
