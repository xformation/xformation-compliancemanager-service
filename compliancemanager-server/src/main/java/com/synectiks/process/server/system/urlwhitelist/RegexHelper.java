/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class to help creating an appropriate regex to be used in a whitelist entry.
 */
public class RegexHelper {
    /**
     * <p>
     * Replaces all placeholders in a "url template" with {@code .*?}, quotes everything else and adds {@code ^} and
     * {@code $} to it.
     * </p>
     * <p>
     * An example:
     * </p>
     * <p>
     * <pre>https://example.com/api/lookup?key=${key}</pre>
     * will become
     * <pre>^\Qhttps://example.com/api/lookup?key=\E.*?$</pre>
     * </p>
     */
    public String createRegexForUrlTemplate(String url, String placeholder) {
        String transformedUrl = Arrays.stream(StringUtils.splitByWholeSeparator(url, placeholder))
                .map(part -> StringUtils.isBlank(part) ? part : Pattern.quote(part))
                .collect(Collectors.joining(".*?"));
        return "^" + transformedUrl + "$";
    }

    /**
     * Quotes the url and adds a {@code ^} and {@code $}.
     */
    public String createRegexForUrl(String url) {
        return "^" + Pattern.quote(url) + "$";
    }
}
