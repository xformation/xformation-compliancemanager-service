/*
 * */
package com.synectiks.process.common.security.permissions;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.grn.GRN;

import org.apache.shiro.authz.Permission;

@AutoValue
public abstract class GRNPermission implements Permission {
    public abstract String type();

    public abstract GRN target();

    public static GRNPermission create(String type, GRN target) {
        return new AutoValue_GRNPermission(type, target);
    }

    @Override
    public boolean implies(Permission p) {
        // GRNPermissions only supports comparisons with other GRNPermissions
        if (!(p instanceof GRNPermission)) {
            return false;
        }
        GRNPermission other = (GRNPermission) p;

        return (other.type().equals(type()) && other.target().equals(target()));
    }

    @JsonValue
    // This string representation is used in the UserSummary and exported to the frontend
    public String jsonValue() {
        return type() + ":" + target().toString();
    }
}
