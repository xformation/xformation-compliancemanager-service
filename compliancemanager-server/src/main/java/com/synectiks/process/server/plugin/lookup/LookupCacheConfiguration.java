/*
 * */
package com.synectiks.process.server.plugin.lookup;

import com.google.common.collect.Multimap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = LookupCacheConfiguration.TYPE_FIELD,
        visible = true,
        defaultImpl = FallbackCacheConfig.class)
public interface LookupCacheConfiguration {
    String TYPE_FIELD = "type";

    @JsonProperty(TYPE_FIELD)
    String type();

    /**
     * <p>Override this method to check for logical errors in the configuration, such as missing
     * files, or invalid combinations of options. Prefer validation annotations for simple
     * per-property validations rules, such as min/max values, non-empty strings etc. </p>
     *
     * <p> By default the configuration has no extra validation errors (i.e. the result of this
     * method is {@link Optional#empty()}. </p>
     *
     * <p>Returning failing validations here <b>does not</b> prevent saving the configuration!</p>
     *
     * @return optionally map of property name to error messages
     */
    @JsonIgnore
    default Optional<Multimap<String, String>> validate() {
        return Optional.empty();
    }
}
