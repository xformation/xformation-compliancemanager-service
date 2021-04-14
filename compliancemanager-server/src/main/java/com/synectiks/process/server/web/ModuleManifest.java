/*
 * */
package com.synectiks.process.server.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class ModuleManifest {
    @JsonProperty("files")
    public abstract ModuleFiles files();

    @JsonCreator
    public static ModuleManifest create(@JsonProperty("files") ModuleFiles files) {
        return new AutoValue_ModuleManifest(files);
    }
}
