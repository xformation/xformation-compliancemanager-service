/*
 * */
package com.synectiks.process.common.freeenterprise;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FreeLicenseInfo {
    public enum Status {
        @JsonProperty("absent")
        ABSENT,
        @JsonProperty("staged")
        STAGED,
        @JsonProperty("installed")
        INSTALLED
    }

    private static final String FIELD_LICENSE_STATUS = "license_status";

    @JsonProperty(FIELD_LICENSE_STATUS)
    public abstract Status licenseStatus();

    public static FreeLicenseInfo absent() {
        return create(Status.ABSENT);
    }

    public static FreeLicenseInfo staged() {
        return create(Status.STAGED);
    }

    public static FreeLicenseInfo installed() {
        return create(Status.INSTALLED);
    }

    @JsonCreator
    public static FreeLicenseInfo create(@JsonProperty(FIELD_LICENSE_STATUS) Status licenseStatus) {
        return new AutoValue_FreeLicenseInfo(licenseStatus);
    }
}
