/*
 * */
package com.synectiks.process.server.rest.resources.system.logs;

import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.rest.resources.system.logs.LoggersResource;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggersResourceTest {
    private LoggersResource resource;

    public LoggersResourceTest() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Before
    public void setUp() throws Exception {
        resource = new LoggersResource();
    }

    @Test
    public void setLoggerLevelOnlySetsLoggersLevel() throws Exception {
        final String parentLoggerName = "LoggersResourceTest";
        final String loggerName = "LoggersResourceTest.setLoggerLevelOnlySetsLoggersLevel";
        final Level originalLevel = resource.getLoggerLevel(loggerName);
        final Level parentLevel = resource.getLoggerLevel(parentLoggerName);
        assertThat(originalLevel).isEqualTo(parentLevel);
        final Level expectedLevel = Level.TRACE;
        assertThat(originalLevel).isNotEqualTo(expectedLevel);

        resource.setLoggerLevel(loggerName, expectedLevel);
        assertThat(resource.getLoggerLevel(parentLoggerName)).isEqualTo(parentLevel);
        assertThat(resource.getLoggerLevel(loggerName)).isEqualTo(expectedLevel);

        resource.setLoggerLevel(loggerName, originalLevel);
        assertThat(resource.getLoggerLevel(parentLoggerName)).isEqualTo(parentLevel);
        assertThat(resource.getLoggerLevel(loggerName)).isEqualTo(originalLevel);
    }
}
