/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.database.CollectionName;

import org.joda.time.DateTime;
import org.mongojack.Id;
import org.mongojack.ObjectId;

import javax.annotation.Nullable;
import java.util.List;

@AutoValue
@JsonAutoDetect
@CollectionName("collector_actions")
public abstract class CollectorActions {
    @JsonProperty("id")
    @Nullable
    @Id
    @ObjectId
    public abstract String id();

    @JsonProperty("sidecar_id")
    public abstract String sidecarId();

    @JsonProperty("created")
    public abstract DateTime created();

    @JsonProperty("action")
    public abstract List<CollectorAction> action();

    @JsonCreator
    public static CollectorActions create(@JsonProperty("id") @Id @ObjectId String id,
                                          @JsonProperty("sidecar_id") String sidecarId,
                                          @JsonProperty("created") DateTime created,
                                          @JsonProperty("action") List<CollectorAction> action) {
        return new AutoValue_CollectorActions(id, sidecarId, created, action);
    }

    public static CollectorActions create(String sidecar_id,
                                          DateTime created,
                                          List<CollectorAction> action) {
        return create(
                new org.bson.types.ObjectId().toHexString(),
                sidecar_id,
                created,
                action);
    }
}
