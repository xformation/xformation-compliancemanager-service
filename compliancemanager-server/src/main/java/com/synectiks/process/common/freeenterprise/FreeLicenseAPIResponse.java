/*
 * */
package com.synectiks.process.common.freeenterprise;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonIgnoreProperties("version")
public abstract class FreeLicenseAPIResponse {
    @JsonProperty("license")
    public abstract String licenseString();

    @JsonCreator
    public static FreeLicenseAPIResponse create(@JsonProperty("license") String licenseString) {
        return new AutoValue_FreeLicenseAPIResponse(licenseString);
    }
}
