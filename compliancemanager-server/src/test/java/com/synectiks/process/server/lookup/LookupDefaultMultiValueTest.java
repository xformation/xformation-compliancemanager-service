/*
 * */
package com.synectiks.process.server.lookup;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.lookup.LookupDefaultMultiValue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LookupDefaultMultiValueTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createMulti() throws Exception {
        assertThat(LookupDefaultMultiValue.create("{}", LookupDefaultMultiValue.Type.OBJECT).value())
                .isInstanceOf(Map.class)
                .isEqualTo(Collections.emptyMap());
        assertThat(LookupDefaultMultiValue.create("{\"hello\":\"world\",\"number\":42}", LookupDefaultMultiValue.Type.OBJECT).value())
                .isInstanceOf(Map.class)
                .isEqualTo(ImmutableMap.of("hello", "world", "number", 42));

        assertThat(LookupDefaultMultiValue.create("something", LookupDefaultMultiValue.Type.NULL).value())
                .isNull();
    }

    @Test
    public void createSingleString() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        LookupDefaultMultiValue.create("foo", LookupDefaultMultiValue.Type.STRING);
    }

    @Test
    public void createSingleNumber() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        LookupDefaultMultiValue.create("42", LookupDefaultMultiValue.Type.NUMBER);
    }

    @Test
    public void createSingleBoolean() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        LookupDefaultMultiValue.create("true", LookupDefaultMultiValue.Type.BOOLEAN);
    }
}