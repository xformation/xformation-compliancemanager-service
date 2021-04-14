/*
 * */
package com.synectiks.process.server.shared.buffers;

import org.junit.Test;
import org.slf4j.Logger;

import com.synectiks.process.server.shared.buffers.LoggingExceptionHandler;

import static org.mockito.Mockito.mock;

public class LoggingExceptionHandlerTest {
    @Test
    public void testHandleEventException() throws Exception {
        final Logger logger = mock(Logger.class);
        final LoggingExceptionHandler handler = new LoggingExceptionHandler(logger);
        handler.handleEventException(new RuntimeException(), -1, null);
        handler.handleEventException(new RuntimeException(), -1, new Object() {
            @Override
            public String toString() {
                throw new NullPointerException();
            }
        });
    }
}
