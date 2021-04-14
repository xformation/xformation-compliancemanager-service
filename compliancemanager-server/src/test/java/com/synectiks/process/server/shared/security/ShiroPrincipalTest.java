/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.subject.Subject;
import org.junit.Test;

import com.synectiks.process.server.shared.security.ShiroPrincipal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShiroPrincipalTest {
    @Test
    public void testGetNameWithNull() throws Exception {
        final Subject subject = mock(Subject.class);
        final ShiroPrincipal shiroPrincipal = new ShiroPrincipal(subject);

        assertThat(shiroPrincipal.getName()).isNull();
    }

    @Test
    public void testGetName() throws Exception {
        final Subject subject = mock(Subject.class);
        when(subject.getPrincipal()).thenReturn("test");
        final ShiroPrincipal shiroPrincipal = new ShiroPrincipal(subject);

        assertThat(shiroPrincipal.getName()).isEqualTo("test");
    }

    @Test
    public void testGetSubject() throws Exception {
        final Subject subject = mock(Subject.class);
        final ShiroPrincipal shiroPrincipal = new ShiroPrincipal(subject);

        assertThat(shiroPrincipal.getSubject()).isSameAs(subject);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullSubject() throws Exception {
        new ShiroPrincipal(null);
    }
}