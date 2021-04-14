/*
 * */
package com.synectiks.process.server.audit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.synectiks.process.server.audit.AuditEventType;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditEventTypeTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testStringType() throws Exception {
        final AuditEventType type = AuditEventType.create("namespace:object:action");

        assertThat(type.namespace()).isEqualTo("namespace");
        assertThat(type.object()).isEqualTo("object");
        assertThat(type.action()).isEqualTo("action");
    }

    @Test
    public void testStringTypeWithMoreColons() throws Exception {
        final AuditEventType type = AuditEventType.create("namespace:object:action:foo");

        assertThat(type.namespace()).isEqualTo("namespace");
        assertThat(type.object()).isEqualTo("object");
        assertThat(type.action()).isEqualTo("action:foo");
    }

    @Test
    public void testToTypeString() throws Exception {
        final AuditEventType type = AuditEventType.create("namespace:object:action");

        assertThat(type.toTypeString()).isEqualTo("namespace:object:action");
    }

    @Test
    public void testInvalid1() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        AuditEventType.create("foo");
    }

    @Test
    public void testInvalid2() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        AuditEventType.create("");
    }

    @Test
    public void testInvalid3() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        AuditEventType.create(null);
    }
}