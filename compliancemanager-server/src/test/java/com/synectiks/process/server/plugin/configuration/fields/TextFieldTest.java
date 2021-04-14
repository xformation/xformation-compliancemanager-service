/*
 * */
package com.synectiks.process.server.plugin.configuration.fields;

import org.junit.Test;

import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TextFieldTest {
    @Test
    public void testGetFieldType() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        assertEquals(TextField.FIELD_TYPE, f.getFieldType());
    }

    @Test
    public void testGetName() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        assertEquals("test", f.getName());
    }

    @Test
    public void testGetHumanName() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        assertEquals("Name", f.getHumanName());
    }

    @Test
    public void testGetDescription() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        assertEquals("description", f.getDescription());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        assertEquals("default", f.getDefaultValue());
    }

    @Test
    public void testIsOptional() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description", ConfigurationField.Optional.OPTIONAL);
        assertEquals(ConfigurationField.Optional.OPTIONAL, f.isOptional());

        TextField f2 = new TextField("test", "Name", "default", "description", ConfigurationField.Optional.NOT_OPTIONAL);
        assertEquals(ConfigurationField.Optional.NOT_OPTIONAL, f2.isOptional());
    }

    @Test
    public void testGetAttributes() throws Exception {
        TextField f = new TextField("test", "Name", "default", "description");
        assertEquals(0, f.getAttributes().size());

        TextField f1 = new TextField("test", "Name", "default", "description", TextField.Attribute.IS_PASSWORD);
        assertEquals(1, f1.getAttributes().size());
        assertTrue(f1.getAttributes().contains("is_password"));
    }
}
