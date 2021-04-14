/*
 * */
package com.synectiks.process.server.lookup;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.lookup.LookupTable;
import com.synectiks.process.server.lookup.LookupTableService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
public class LookupTableServiceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private LookupTableService service;
    @Mock
    private LookupTable table;
    private LookupTableService.Function function;

    @Before
    public void setUp() throws Exception {
        this.function = new LookupTableService.Function(service, "table");

        when(service.getTable("table")).thenReturn(table);
    }

    @Test
    public void functionSetValue() {
        function.setValue("key", "value");

        assertThatThrownBy(() -> function.setValue("key", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.setValue(null, "value2")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.setValue(null, null)).isInstanceOf(NullPointerException.class);

        verify(table, times(1)).setValue("key", "value");
        verify(table, never()).setValue("key", null);
        verify(table, never()).setValue(null, "value2");
        verify(table, never()).setValue(null, null);
    }

    @Test
    public void functionSetStringList() {
        function.setStringList("key", ImmutableList.of("hello", "world"));
        function.setStringList("key", Arrays.asList("with", "empty", "", "and", null));

        assertThatThrownBy(() -> function.setStringList("key", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.setStringList(null, ImmutableList.of("none"))).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.setStringList(null, null)).isInstanceOf(NullPointerException.class);

        verify(table, times(1)).setStringList("key", ImmutableList.of("hello", "world"));
        verify(table, times(1)).setStringList("key", ImmutableList.of("with", "empty", "and"));

        verify(table, never()).setStringList("key", null);
        verify(table, never()).setStringList(null, ImmutableList.of("none"));
        verify(table, never()).setStringList(null, null);
        verify(table, never()).setStringList("key", Arrays.asList("with", "empty", "", "and", null));
    }

    @Test
    public void functionAddStringList() {
        function.addStringList("key", ImmutableList.of("hello", "world"), false);
        function.addStringList("key", Arrays.asList("with", "empty", "", "and", null), false);

        assertThatThrownBy(() -> function.addStringList("key", null, false)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.addStringList(null, ImmutableList.of("none"), false)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.addStringList(null, null, false)).isInstanceOf(NullPointerException.class);

        verify(table, times(1)).addStringList("key", ImmutableList.of("hello", "world"), false);
        verify(table, times(1)).addStringList("key", ImmutableList.of("with", "empty", "and"), false);

        verify(table, never()).addStringList("key", null, false);
        verify(table, never()).addStringList(null, ImmutableList.of("none"), false);
        verify(table, never()).addStringList(null, null, false);
        verify(table, never()).addStringList("key", Arrays.asList("with", "empty", "", "and", null), false);
    }

    @Test
    public void functionRemoveStringList() {
        function.removeStringList("key", ImmutableList.of("hello", "world"));
        function.removeStringList("key", Arrays.asList("with", "empty", "", "and", null));

        assertThatThrownBy(() -> function.removeStringList("key", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.removeStringList(null, ImmutableList.of("none"))).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> function.removeStringList(null, null)).isInstanceOf(NullPointerException.class);

        verify(table, times(1)).removeStringList("key", ImmutableList.of("hello", "world"));
        verify(table, times(1)).removeStringList("key", ImmutableList.of("with", "empty", "and"));

        verify(table, never()).removeStringList("key", null);
        verify(table, never()).removeStringList(null, ImmutableList.of("none"));
        verify(table, never()).removeStringList(null, null);
        verify(table, never()).removeStringList("key", Arrays.asList("with", "empty", "", "and", null));
    }

    @Test
    public void functionClearKey() {
        function.clearKey("key");

        assertThatThrownBy(() -> function.clearKey(null)).isInstanceOf(NullPointerException.class);

        verify(table, times(1)).clearKey("key");
        verify(table, never()).clearKey(null);
    }
}
