/*
 * */
package com.synectiks.process.server.grok;

import com.synectiks.process.server.grok.krakens.GrokUtils;
import com.synectiks.process.server.grok.krakens.exception.GrokException;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.ValidationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface GrokPatternService {
    GrokPattern load(String patternId) throws NotFoundException;

    Optional<GrokPattern> loadByName(String name);

    Set<GrokPattern> bulkLoad(Collection<String> patternIds);

    Set<GrokPattern> loadAll();

    GrokPattern save(GrokPattern pattern) throws ValidationException;

    GrokPattern update(GrokPattern pattern) throws ValidationException;

    List<GrokPattern> saveAll(Collection<GrokPattern> patterns, boolean replace) throws ValidationException;

    Map<String, Object> match(GrokPattern pattern, String sampleData) throws GrokException;

    boolean validate(GrokPattern pattern) throws GrokException;

    boolean validateAll(Collection<GrokPattern> patterns) throws GrokException;

    int delete(String patternId);

    int deleteAll();

    static Set<String> extractPatternNames(String namedPattern) {
        final Set<String> result = new HashSet<>();
        // We have to use java.util.Regex here to get the names because ".find()" on the "com.google.code.regexp.Matcher"
        // would run in an endless loop.
        final Set<String> namedGroups = GrokUtils.getNameGroups(GrokUtils.GROK_PATTERN.namedPattern());
        final Matcher matcher = Pattern.compile(GrokUtils.GROK_PATTERN.namedPattern()).matcher(namedPattern);
        while (matcher.find()) {
            final Map<String, String> group = namedGroups(matcher, namedGroups);
            final String patternName = group.get("pattern");
            result.add(patternName);
        }
        return result;
    }

    static Map<String, String> namedGroups(Matcher matcher, Set<String> groupNames) {
        Map<String, String> namedGroups = new LinkedHashMap<>();
        for (String groupName : groupNames) {
            String groupValue = matcher.group(groupName);
            namedGroups.put(groupName, groupValue);
        }
        return namedGroups;
    }
}
