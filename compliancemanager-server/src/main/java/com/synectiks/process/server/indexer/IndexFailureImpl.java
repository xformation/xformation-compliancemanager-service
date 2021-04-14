/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.database.PersistedImpl;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.database.validators.Validator;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Map;

@CollectionName("index_failures")
public class IndexFailureImpl extends PersistedImpl implements IndexFailure {

    public IndexFailureImpl(Map<String, Object> fields) {
        super(fields);
    }

    protected IndexFailureImpl(ObjectId id, Map<String, Object> fields) {
        super(id, fields);
    }

    @Override
    public String letterId() {
        return (String)fields.get("letter_id");
    }

    @Override
    public Map<String, Object> asMap() {
        return ImmutableMap.<String, Object>builder()
                .put("timestamp", Tools.getISO8601String((DateTime) fields.get("timestamp")))
                .put("letter_id", fields.get("letter_id"))
                .put("message", fields.get("message"))
                .put("index", fields.get("index"))
                .put("type", fields.get("type"))
                .build();
    }

    @Override
    public Map<String, Validator> getValidations() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Validator> getEmbeddedValidations(String key) {
        return Collections.emptyMap();
    }

}
