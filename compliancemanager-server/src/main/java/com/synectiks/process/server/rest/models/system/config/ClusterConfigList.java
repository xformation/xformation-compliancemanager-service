/*
 * */
package com.synectiks.process.server.rest.models.system.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ClusterConfigList {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract Set<String> classes();

    public static ClusterConfigList create(Collection<String> classes) {
        return new AutoValue_ClusterConfigList(classes.size(), ImmutableSet.copyOf(classes));
    }

    public static ClusterConfigList createFromClass(Collection<Class<?>> classes) {
        return create(classes.stream().map(Class::getCanonicalName).collect(Collectors.toSet()));
    }
}
