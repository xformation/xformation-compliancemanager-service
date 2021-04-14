/*
 * */
package com.synectiks.process.server.filters;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.filters.StaticFieldFilter;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.shared.SuppressForbidden;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class StaticFieldFilterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private InputService inputService;
    @Mock
    private Input input;

    @Test
    @SuppressForbidden("Executors#newSingleThreadExecutor() is okay for tests")
    public void testFilter() throws Exception {
        Message msg = new Message("hello", "junit", Tools.nowUTC());
        msg.setSourceInputId("someid");

        when(input.getId()).thenReturn("someid");
        when(inputService.all()).thenReturn(Collections.singletonList(input));
        when(inputService.find(eq("someid"))).thenReturn(input);
        when(inputService.getStaticFields(eq(input)))
                .thenReturn(Collections.singletonList(Maps.immutableEntry("foo", "bar")));

        final StaticFieldFilter filter = new StaticFieldFilter(inputService, new EventBus(), Executors.newSingleThreadScheduledExecutor());
        filter.filter(msg);

        assertEquals("hello", msg.getMessage());
        assertEquals("junit", msg.getSource());
        assertEquals("bar", msg.getField("foo"));
    }

    @Test
    @SuppressForbidden("Executors#newSingleThreadExecutor() is okay for tests")
    public void testFilterIsNotOverwritingExistingKeys() throws Exception {
        Message msg = new Message("hello", "junit", Tools.nowUTC());
        msg.addField("foo", "IWILLSURVIVE");

        final StaticFieldFilter filter = new StaticFieldFilter(inputService, new EventBus(), Executors.newSingleThreadScheduledExecutor());
        filter.filter(msg);

        assertEquals("hello", msg.getMessage());
        assertEquals("junit", msg.getSource());
        assertEquals("IWILLSURVIVE", msg.getField("foo"));
    }
}
