/*
 * */
package com.synectiks.process.server.cluster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.database.PersistedImpl;
import com.synectiks.process.server.plugin.database.validators.Validator;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@CollectionName("nodes")
public class NodeImpl extends PersistedImpl implements Node {

    protected NodeImpl(Map<String, Object> fields) {
        super(fields);
    }

    protected NodeImpl(ObjectId id, Map<String, Object> fields) {
        super(id, fields);
    }

    @Override
    public String getNodeId() {
        return (String) fields.get("node_id");
    }

    @Override
    @JsonProperty("is_master")
    public boolean isMaster() {
        return (Boolean) fields.get("is_master");
    }

    @Override
    public String getTransportAddress() {
        return (String) fields.get("transport_address");
    }

    @Override
    public DateTime getLastSeen() {
        return new DateTime(((Integer) fields.getOrDefault("last_seen", 0)) * 1000L, DateTimeZone.UTC);
    }

    @Override
    public String getShortNodeId() {
        return getNodeId().split("-")[0];
    }

    @Override
    public Type getType() {
        if (!fields.containsKey("type")) {
            return Type.SERVER;
        }

        return Type.valueOf(fields.get("type").toString().toUpperCase(Locale.ENGLISH));
    }

    @Override
    public String getHostname() {
        return (String)fields.get("hostname");
    }

    @Override
    @JsonIgnore
    public Map<String, Validator> getValidations() {
        return Collections.emptyMap();
    }

    @Override
    @JsonIgnore
    public Map<String, Validator> getEmbeddedValidations(String key) {
        return Collections.emptyMap();
    }

}
