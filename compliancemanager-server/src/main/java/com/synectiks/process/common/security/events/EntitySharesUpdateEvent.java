/*
 * */
package com.synectiks.process.common.security.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.server.plugin.database.users.User;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@AutoValue
@JsonAutoDetect
public abstract class EntitySharesUpdateEvent {
    public abstract User user();
    public abstract GRN entity();
    public abstract ImmutableList<Share> creates();
    public abstract ImmutableList<Share> deletes();
    public abstract ImmutableList<Share> updates();

    public static EntitySharesUpdateEvent create(User user, GRN entity, List<Share> creates, List<Share> deletes, List<Share> updates) {
        return builder()
                .user(user)
                .entity(entity)
                .creates(creates)
                .deletes(deletes)
                .updates(updates)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_EntitySharesUpdateEvent.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract ImmutableList.Builder<Share> createsBuilder();
        public abstract ImmutableList.Builder<Share> deletesBuilder();
        public abstract ImmutableList.Builder<Share> updatesBuilder();

        public abstract Builder user(User user);

        public abstract Builder entity(GRN entity);

        public Builder addCreates(GRN grantee, Capability capability) {
            createsBuilder().add(Share.create(grantee, capability, null));
            return this;
        }
        public Builder addDeletes(GRN grantee, Capability capability) {
            deletesBuilder().add(Share.create(grantee, capability, null));
            return this;
        }
        public Builder addUpdates(GRN grantee, Capability capability, Capability formerCapability) {
            updatesBuilder().add(Share.create(grantee, capability, formerCapability));
            return this;
        }
        public abstract Builder creates(List<Share> creates);
        public abstract Builder deletes(List<Share> deletes);
        public abstract Builder updates(List<Share> updates);

        public abstract EntitySharesUpdateEvent build();

    }

    @AutoValue
    @JsonAutoDetect
    public abstract static class Share {
        public abstract GRN grantee();
        public abstract Capability capability();
        public abstract Optional<Capability> formerCapability();

        public static Share create(GRN grantee, Capability capability, @Nullable Capability formerCapability) {
            return new AutoValue_EntitySharesUpdateEvent_Share(grantee, capability, Optional.ofNullable(formerCapability));
        }

    }
}
