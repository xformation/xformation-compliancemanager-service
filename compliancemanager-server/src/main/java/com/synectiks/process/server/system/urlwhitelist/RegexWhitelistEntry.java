/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class RegexWhitelistEntry implements WhitelistEntry {
    private Pattern pattern;

    @JsonCreator
    public static RegexWhitelistEntry create(@JsonProperty("id") String id, @JsonProperty("title") String title,
            @JsonProperty("value") String value) {

        // compile the pattern early so that we can catch illegal expressions asap
        final Pattern pattern;
        try {
            pattern = Pattern.compile(value, Pattern.DOTALL);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(
                    "Cannot create whitelist entry for invalid regular expression '" + value + "': " + e.getMessage(),
                    e);
        }
        final RegexWhitelistEntry whitelistEntry = new AutoValue_RegexWhitelistEntry(id, Type.REGEX, title, value);
        whitelistEntry.pattern = pattern;
        return whitelistEntry;
    }

    @Override
    public boolean isWhitelisted(String url) {
        return pattern.matcher(url).find();
    }
}
