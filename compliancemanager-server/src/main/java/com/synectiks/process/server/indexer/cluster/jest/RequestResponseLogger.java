/*
 * */
package com.synectiks.process.server.indexer.cluster.jest;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RequestResponseLogger implements HttpResponseInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseLogger.class);

    private final Logger logger;

    public RequestResponseLogger() {
        this(LOG);
    }

    public RequestResponseLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        // Avoid work if TRACE is not enabled for this class
        if (logger.isTraceEnabled()) {
            final StatusLine statusLine = response.getStatusLine();
            final HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            final HttpRequest httpRequest = (HttpRequest) context
                    .getAttribute(HttpCoreContext.HTTP_REQUEST);
            final RequestLine request = httpRequest.getRequestLine();
            logger.trace("[{} {}]: {} {}{}",
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase(),
                    request.getMethod(),
                    targetHost.toURI(),
                    request.getUri()
            );
        }
    }
}
