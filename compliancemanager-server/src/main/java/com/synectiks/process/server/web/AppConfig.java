/*
 * */
package com.synectiks.process.server.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.net.URI;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AppConfig {
    @JsonProperty("xfalertServerUrl")
    public abstract URI serverUri();

    @JsonProperty("xfalertAppPathPrefix")
    public abstract String appPathPrefix();

    @JsonCreator
    public static AppConfig create(URI serverUri) {
        return new AutoValue_AppConfig(serverUri, "");
    }
}
