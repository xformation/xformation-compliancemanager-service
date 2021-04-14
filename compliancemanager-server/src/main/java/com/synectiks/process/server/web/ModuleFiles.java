/*
 * */
package com.synectiks.process.server.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ModuleFiles {
    @JsonProperty("js")
    public abstract List<String> jsFiles();

    @JsonProperty("css")
    public abstract List<String> cssFiles();

    @JsonCreator
    public static ModuleFiles create(@JsonProperty("js") List<String> jsFiles,
                                     @JsonProperty("css") List<String> cssFiles) {
        return new AutoValue_ModuleFiles(jsFiles, cssFiles);
    }
}
