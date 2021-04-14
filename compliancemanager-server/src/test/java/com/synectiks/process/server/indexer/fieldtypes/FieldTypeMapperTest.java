/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.indexer.fieldtypes.FieldTypeMapper;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.synectiks.process.server.indexer.fieldtypes.FieldTypes.Type.createType;
import static org.assertj.core.api.Assertions.assertThat;

public class FieldTypeMapperTest {
    private FieldTypeMapper mapper;

    @Before
    public void setUp() throws Exception {
        this.mapper = new FieldTypeMapper();
    }

    private void assertMapping(String esType, String glType, String... properties) {
        assertThat(mapper.mapType(esType))
                .isPresent().get()
                .isEqualTo(createType(glType, copyOf(properties)));
    }

    @Test
    public void mappings() {
        assertMapping("text", "string", "full-text-search");
        assertMapping("keyword", "string", "enumerable");

        assertMapping("long", "long", "numeric", "enumerable");
        assertMapping("integer", "int", "numeric", "enumerable");
        assertMapping("short", "short", "numeric", "enumerable");
        assertMapping("byte", "byte", "numeric", "enumerable");
        assertMapping("double", "double", "numeric", "enumerable");
        assertMapping("float", "float", "numeric", "enumerable");
        assertMapping("half_float", "float", "numeric", "enumerable");
        assertMapping("scaled_float", "float", "numeric", "enumerable");

        assertMapping("date", "date", "enumerable");
        assertMapping("boolean", "boolean", "enumerable");
        assertMapping("binary", "binary");
        assertMapping("geo_point", "geo-point");
        assertMapping("ip", "ip", "enumerable");
    }
}