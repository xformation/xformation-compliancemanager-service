/*
 * */
package com.synectiks.process.server.system.activities;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.database.PersistedImpl;
import com.synectiks.process.server.database.validators.DateValidator;
import com.synectiks.process.server.database.validators.FilledStringValidator;
import com.synectiks.process.server.plugin.database.validators.Validator;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Collections;
import java.util.Map;

@CollectionName("system_messages")
public class SystemMessageImpl extends PersistedImpl implements SystemMessage {

    private final String caller;
    private final String content;
    private final DateTime timestamp;
    private final String nodeId;

    public SystemMessageImpl(Map<String, Object> fields) {
        super(fields);

        this.caller = fields.get("caller").toString();
        this.content = fields.get("content").toString();
        this.timestamp = new DateTime(fields.get("timestamp").toString(), DateTimeZone.UTC);
        this.nodeId = fields.get("node_id").toString();
    }

    protected SystemMessageImpl(ObjectId id, Map<String, Object> fields) {
        super(id, fields);

        this.caller = (String) fields.get("caller");
        this.content = (String) fields.get("content");
        this.timestamp = new DateTime(fields.get("timestamp"), DateTimeZone.UTC);
        this.nodeId = (String) fields.get("node_id");
    }

    @Override
    public String getCaller() {
        return caller;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public Map<String, Validator> getValidations() {
        return ImmutableMap.of(
                "caller", new FilledStringValidator(),
                "content", new FilledStringValidator(),
                "node_id", new FilledStringValidator(),
                "timestamp", new DateValidator());
    }

    @Override
    public Map<String, Validator> getEmbeddedValidations(String key) {
        return Collections.emptyMap();
    }

}