/*
 * */
package com.synectiks.process.server.database;

import com.google.common.collect.Maps;
import com.synectiks.process.server.database.PersistedImpl;
import com.synectiks.process.server.plugin.database.Persisted;
import com.synectiks.process.server.plugin.database.validators.Validator;

import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class PersistedImplTest {
    private static class PersistedImplSUT extends PersistedImpl {
        PersistedImplSUT(Map<String, Object> fields) {
            super(fields);
        }

        PersistedImplSUT(ObjectId id, Map<String, Object> fields) {
            super(id, fields);
        }

        @Override
        public Map<String, Validator> getValidations() {
            return null;
        }

        @Override
        public Map<String, Validator> getEmbeddedValidations(String key) {
            return null;
        }
    }

    @Test
    public void testConstructorWithFieldsOnly() throws Exception {
        Map<String, Object> fields = Maps.newHashMap();
        Persisted persisted = new PersistedImplSUT(fields);
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertFalse(persisted.getId().isEmpty());
    }

    @Test
    public void testConstructorWithFieldsAndId() throws Exception {
        Map<String, Object> fields = Maps.newHashMap();
        ObjectId id = new ObjectId();
        Persisted persisted = new PersistedImplSUT(id, fields);
        assertNotNull(persisted);
        assertNotNull(persisted.getId());
        assertFalse(persisted.getId().isEmpty());
        assertEquals(id.toString(), persisted.getId());
    }

    @Test
    public void testEqualityForSameRecord() throws Exception {
        Map<String, Object> fields = Maps.newHashMap();
        fields.put("foo", "bar");
        fields.put("bar", 42);

        ObjectId id = new ObjectId();

        Persisted persisted1 = new PersistedImplSUT(id, fields);
        Persisted persisted2 = new PersistedImplSUT(id, fields);

        assertEquals(persisted1, persisted2);
    }
}
