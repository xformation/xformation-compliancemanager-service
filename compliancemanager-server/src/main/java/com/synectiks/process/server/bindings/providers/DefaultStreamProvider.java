/*
 * */
package com.synectiks.process.server.bindings.providers;

import com.synectiks.process.server.gelfclient.util.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class DefaultStreamProvider implements Provider<Stream> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultStreamProvider.class);

    private final StreamService service;

    private AtomicReference<Stream> sharedInstance = new AtomicReference<>();

    @Inject
    private DefaultStreamProvider(StreamService service) {
        this.service = service;
    }

    public void setDefaultStream(Stream defaultStream) {
        LOG.debug("Setting new default stream: {}", defaultStream);
        this.sharedInstance.set(defaultStream);
    }

    @Override
    public Stream get() {
        Stream defaultStream = sharedInstance.get();
        if (defaultStream != null) {
            return defaultStream;
        }

        synchronized (this) {
            defaultStream = sharedInstance.get();
            if (defaultStream != null) {
                return defaultStream;
            }
            int i = 0;
            do {
                try {
                    LOG.debug("Loading shared default stream instance");
                    defaultStream = service.load(Stream.DEFAULT_STREAM_ID);
                } catch (NotFoundException ignored) {
                    if (i % 10 == 0) {
                        LOG.warn("Unable to load default stream, tried {} times, retrying every 500ms. Processing is blocked until this succeeds.", i + 1);
                    }
                    i++;
                    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
                }
            } while (defaultStream == null);
            sharedInstance.set(defaultStream);
        }
        return defaultStream;
    }
}
