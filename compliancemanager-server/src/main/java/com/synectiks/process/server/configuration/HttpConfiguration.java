/*
 * */
package com.synectiks.process.server.configuration;

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.ParameterException;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.ValidatorMethod;
import com.github.joschi.jadconfig.validators.PositiveIntegerValidator;
import com.github.joschi.jadconfig.validators.URIAbsoluteValidator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.synectiks.process.server.plugin.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConfiguration.class);

    private static final int ALERTMANAGER_DEFAULT_PORT = 9000;

    public static final String OVERRIDE_HEADER = "X-alertmanager-Server-URL";
    public static final String PATH_WEB = "";
    public static final String PATH_API = "api/";

    @Parameter(value = "http_bind_address", required = true)
    private HostAndPort httpBindAddress = HostAndPort.fromParts("127.0.0.1", ALERTMANAGER_DEFAULT_PORT);

    @Parameter(value = "http_publish_uri", validator = URIAbsoluteValidator.class)
    private URI httpPublishUri;

    @Parameter(value = "http_enable_cors")
    private boolean httpEnableCors = false;

    @Parameter(value = "http_enable_gzip")
    private boolean httpEnableGzip = true;

    @Parameter(value = "http_max_header_size", required = true, validator = PositiveIntegerValidator.class)
    private int httpMaxHeaderSize = 8192;

    @Parameter(value = "http_thread_pool_size", required = true, validator = PositiveIntegerValidator.class)
    private int httpThreadPoolSize = 16;

    @Parameter(value = "http_selector_runners_count", required = true, validator = PositiveIntegerValidator.class)
    private int httpSelectorRunnersCount = 1;

    @Parameter(value = "http_enable_tls")
    private boolean httpEnableTls = false;

    @Parameter(value = "http_tls_cert_file")
    private Path httpTlsCertFile;

    @Parameter(value = "http_tls_key_file")
    private Path httpTlsKeyFile;

    @Parameter(value = "http_tls_key_password")
    private String httpTlsKeyPassword;

    @Parameter(value = "http_external_uri")
    private URI httpExternalUri;

    public HostAndPort getHttpBindAddress() {
        return httpBindAddress
                .requireBracketsForIPv6()
                .withDefaultPort(ALERTMANAGER_DEFAULT_PORT);
    }

    public String getUriScheme() {
        return isHttpEnableTls() ? "https" : "http";
    }

    @Nullable
    private InetAddress toInetAddress(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            LOG.debug("Couldn't resolve \"{}\"", host, e);
            return null;
        }
    }

    public URI getHttpPublishUri() {
        if (httpPublishUri == null) {
            final URI defaultHttpUri = getDefaultHttpUri();
            LOG.debug("No \"http_publish_uri\" set. Using default <{}>.", defaultHttpUri);
            return defaultHttpUri;
        } else {
            final InetAddress inetAddress = toInetAddress(httpPublishUri.getHost());
            if (Tools.isWildcardInetAddress(inetAddress)) {
                final URI defaultHttpUri = getDefaultHttpUri(httpPublishUri.getPath());
                LOG.warn("\"{}\" is not a valid setting for \"http_publish_uri\". Using default <{}>.", httpPublishUri, defaultHttpUri);
                return defaultHttpUri;
            } else {
                return Tools.normalizeURI(httpPublishUri, httpPublishUri.getScheme(), ALERTMANAGER_DEFAULT_PORT, httpPublishUri.getPath());
            }
        }
    }

    @VisibleForTesting
    URI getDefaultHttpUri() {
        return getDefaultHttpUri("/");
    }

    private URI getDefaultHttpUri(String path) {
        final HostAndPort bindAddress = getHttpBindAddress();

        final URI publishUri;
        final InetAddress inetAddress = toInetAddress(bindAddress.getHost());
        if (inetAddress != null && Tools.isWildcardInetAddress(inetAddress)) {
            final InetAddress guessedAddress;
            try {
                guessedAddress = Tools.guessPrimaryNetworkAddress(inetAddress instanceof Inet4Address);

                if (guessedAddress.isLoopbackAddress()) {
                    LOG.debug("Using loopback address {}", guessedAddress);
                }
            } catch (Exception e) {
                LOG.error("Could not guess primary network address for \"http_publish_uri\". Please configure it in your alertmanager configuration.", e);
                throw new ParameterException("No http_publish_uri.", e);
            }

            try {
                publishUri = new URI(
                        getUriScheme(),
                        null,
                        guessedAddress.getHostAddress(),
                        bindAddress.getPort(),
                        path,
                        null,
                        null
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid http_publish_uri.", e);
            }
        } else {
            try {
                publishUri = new URI(
                        getUriScheme(),
                        null,
                        getHttpBindAddress().getHost(),
                        getHttpBindAddress().getPort(),
                        path,
                        null,
                        null
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid http_publish_uri.", e);
            }
        }

        return publishUri;
    }

    public boolean isHttpEnableCors() {
        return httpEnableCors;
    }

    public boolean isHttpEnableGzip() {
        return httpEnableGzip;
    }

    public int getHttpMaxHeaderSize() {
        return httpMaxHeaderSize;
    }

    public int getHttpThreadPoolSize() {
        return httpThreadPoolSize;
    }

    public int getHttpSelectorRunnersCount() {
        return httpSelectorRunnersCount;
    }

    public boolean isHttpEnableTls() {
        return httpEnableTls;
    }

    public Path getHttpTlsCertFile() {
        return httpTlsCertFile;
    }

    public Path getHttpTlsKeyFile() {
        return httpTlsKeyFile;
    }

    public String getHttpTlsKeyPassword() {
        return httpTlsKeyPassword;
    }

    public URI getHttpExternalUri() {
        return httpExternalUri == null ? getHttpPublishUri() : httpExternalUri;
    }

    @ValidatorMethod
    @SuppressWarnings("unused")
    public void validateHttpBindAddress() throws ValidationException {
        try {
            final String host = getHttpBindAddress().getHost();
            if (!InetAddresses.isInetAddress(host)) {
                final InetAddress inetAddress = InetAddress.getByName(host);
            }
        } catch (IllegalArgumentException | UnknownHostException e) {
            throw new ValidationException(e);
        }
    }

    @ValidatorMethod
    @SuppressWarnings("unused")
    public void validateHttpPublishUriPathEndsWithSlash() throws ValidationException {
        if (!getHttpPublishUri().getPath().endsWith("/")) {
            throw new ValidationException("\"http_publish_uri\" must end with a slash (\"/\")");
        }
    }

    @ValidatorMethod
    @SuppressWarnings("unused")
    public void validateHttpExternalUriPathEndsWithSlash() throws ValidationException {
        if (!getHttpExternalUri().getPath().endsWith("/")) {
            throw new ValidationException("\"http_external_uri\" must end with a slash (\"/\")");
        }
    }

    @ValidatorMethod
    @SuppressWarnings("unused")
    public void validateTlsConfig() throws ValidationException {
        if (isHttpEnableTls()) {
            if (!isRegularFileAndReadable(getHttpTlsKeyFile())) {
                throw new ValidationException("Unreadable or missing HTTP private key: " + getHttpTlsKeyFile());
            }

            if (!isRegularFileAndReadable(getHttpTlsCertFile())) {
                throw new ValidationException("Unreadable or missing HTTP X.509 certificate: " + getHttpTlsCertFile());
            }
        }
    }

    private boolean isRegularFileAndReadable(Path path) {
        return path != null && Files.isRegularFile(path) && Files.isReadable(path);
    }
}
