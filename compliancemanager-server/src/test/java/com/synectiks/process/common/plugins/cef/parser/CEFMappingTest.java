/*
 * */
package com.synectiks.process.common.plugins.cef.parser;

import org.junit.Test;

import com.synectiks.process.common.plugins.cef.parser.CEFMapping;

import static org.junit.Assert.assertEquals;

public class CEFMappingTest {
    @Test
    public void forKeyName() throws Exception {
        for (CEFMapping mapping : CEFMapping.values()) {
            assertEquals(mapping, CEFMapping.forKeyName(mapping.getKeyName()));
        }
    }

    @Test
    public void forFullName() throws Exception {
        for (CEFMapping mapping : CEFMapping.values()) {
            assertEquals(mapping, CEFMapping.forFullName(mapping.getFullName()));
        }
    }
}