/*
 * */
package com.synectiks.process.common.testing.completebackend;

public enum Lifecycle {
    /**
     * Use this, if you can make sure
     * that the individual tests will not interfere with each other, e.g., by creating test data that
     * would affect the outcome of a different test.
     */
    CLASS,
    /**
     * This is the safest
     * way to isolate tests. Test execution will take much longer due to the time it takes to spin up
     * the necessary container, especially the server node itself.
     */
    METHOD {
        @Override
        void afterEach(GraylogBackend backend) {
            backend.fullReset();
        }
    };

    void afterEach(GraylogBackend backend) {
    }
}
