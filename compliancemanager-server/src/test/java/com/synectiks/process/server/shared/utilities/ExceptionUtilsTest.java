/*
 * */
package com.synectiks.process.server.shared.utilities;

import org.junit.Test;

import com.synectiks.process.server.shared.utilities.ExceptionUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionUtilsTest {
    @Test
    public void formatMessageCause() {
        assertThat(ExceptionUtils.formatMessageCause(new Exception())).isNotBlank();
    }
    @Test
    public void getRootCauseMessage() {
        assertThat(ExceptionUtils.getRootCauseMessage(new Exception("cause1", new Exception("root")))).satisfies(m -> {
           assertThat(m).isNotBlank();
           assertThat(m).isEqualTo("root.");
        });
    }
    @Test
    public void getRootCauseOrMessage() {
        assertThat(ExceptionUtils.getRootCauseOrMessage(new Exception("cause1", new Exception("root")))).satisfies(m -> {
            assertThat(m).isNotBlank();
            assertThat(m).isEqualTo("root.");
        });
        assertThat(ExceptionUtils.getRootCauseOrMessage(new Exception("cause1"))).satisfies(m -> {
            assertThat(m).isNotBlank();
            assertThat(m).isEqualTo("cause1.");
        });
        assertThat(ExceptionUtils.getRootCauseOrMessage(new Exception("cause1", new Exception("")))).satisfies(m -> {
            assertThat(m).isNotBlank();
            assertThat(m).isEqualTo("cause1.");
        });
    }
}
