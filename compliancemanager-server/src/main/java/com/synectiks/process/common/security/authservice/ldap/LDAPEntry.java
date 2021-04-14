/*
 * */
package com.synectiks.process.common.security.authservice.ldap;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isBlank;

@AutoValue
public abstract class LDAPEntry {
    public abstract String dn();

    public abstract String base64UniqueId();

    public abstract ImmutableSet<String> objectClasses();

    public abstract ImmutableListMultimap<String, String> attributes();

    public boolean hasAttribute(String key) {
        return attributes().containsKey(toKey(key));
    }

    public Optional<ImmutableList<String>> allAttributeValues(String key) {
        return Optional.ofNullable(attributes().get(toKey(key)));
    }

    public Optional<String> firstAttributeValue(String key) {
        return Optional.ofNullable(attributes().get(toKey(key)))
                .filter(values -> !values.isEmpty())
                .map(values -> values.get(0));
    }

    /**
     * Returns the given attribute or throws an exception if the value for the given key is null or blank.
     *
     * @param key the attribute key
     * @return the value
     * @throws IllegalArgumentException when attribute value is null or blank
     */
    public String nonBlankAttribute(String key) {
        final String value = firstAttributeValue(key).orElse(null);
        if (isBlank(value)) {
            throw new IllegalArgumentException("Value for key <" + key + "> cannot be blank");
        }
        return value;
    }

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public static Builder create() {
            return new AutoValue_LDAPEntry.Builder()
                    .objectClasses(Collections.emptySet());
        }

        public abstract Builder dn(String dn);

        public abstract Builder base64UniqueId(String base64UniqueId);

        public abstract Builder objectClasses(Collection<String> objectClasses);

        public abstract ImmutableListMultimap.Builder<String, String> attributesBuilder();

        public Builder addAttribute(String key, String value) {
            if (value != null) {
                // Immutable maps can't handle null values
                attributesBuilder().put(toKey(key), value);
            }
            return this;
        }

        public abstract LDAPEntry build();

    }

    private static String toKey(String key) {
        checkArgument(!isBlank(key), "key cannot be blank");
        return key.toLowerCase(Locale.ENGLISH);
    }
}
