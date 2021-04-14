/*
 * */
package com.synectiks.process.server.utilities;

import com.google.common.util.concurrent.Service;

import java.util.concurrent.CountDownLatch;

/**
 * Counts down the given latch when the service has finished "starting", i.e. either it runs fine or failed during startup.
 *
 */
public class LatchUpdaterListener extends Service.Listener {
    private final CountDownLatch latch;

    public LatchUpdaterListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void running() {
        latch.countDown();
    }

    @Override
    public void failed(Service.State from, Throwable failure) {
        latch.countDown();
    }
}
