/*
 * */
package com.synectiks.process.server.plugin.configuration.fields;

import org.junit.Test;

import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.NumberField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NumberFieldTest {

    @Test
    public void testGetFieldType() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals(NumberField.FIELD_TYPE, f.getFieldType());
    }

    @Test
    public void testGetName() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals("test", f.getName());
    }

    @Test
    public void testGetHumanName() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals("Name", f.getHumanName());
    }

    @Test
    public void testGetDescription() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals("foo", f.getDescription());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        NumberField f = new NumberField("test", "Name", 9001, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals(9001, f.getDefaultValue());
    }

    @Test
    public void testIsOptional() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals(ConfigurationField.Optional.NOT_OPTIONAL, f.isOptional());

        NumberField f2 = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.OPTIONAL);
        assertEquals(ConfigurationField.Optional.OPTIONAL, f2.isOptional());
    }

    @Test
    public void testGetAttributes() throws Exception {
        NumberField f = new NumberField("test", "Name", 0, "foo", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals(0, f.getAttributes().size());

        NumberField f1 = new NumberField("test", "Name", 0, "foo", NumberField.Attribute.IS_PORT_NUMBER);
        assertEquals(1, f1.getAttributes().size());
        assertTrue(f1.getAttributes().contains("is_port_number"));

        NumberField f2 = new NumberField("test", "Name", 0, "foo", NumberField.Attribute.IS_PORT_NUMBER, NumberField.Attribute.ONLY_POSITIVE);
        assertEquals(2, f2.getAttributes().size());
        assertTrue(f2.getAttributes().contains("is_port_number"));
        assertTrue(f2.getAttributes().contains("only_positive"));
    }

}
