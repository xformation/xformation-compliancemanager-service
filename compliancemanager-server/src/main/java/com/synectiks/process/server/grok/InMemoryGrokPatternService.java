/*
 * */
package com.synectiks.process.server.grok;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.plugin.database.ValidationException;

import com.synectiks.process.server.grok.krakens.Grok;
import com.synectiks.process.server.grok.krakens.GrokCompiler;
import com.synectiks.process.server.grok.krakens.exception.GrokException;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class InMemoryGrokPatternService implements GrokPatternService {
    // poor man's id generator
    private final AtomicLong idGen = new AtomicLong(0);

    private final ConcurrentMap<String, GrokPattern> store = new ConcurrentHashMap<>();
    private final ClusterEventBus clusterBus;

    @Inject
    public InMemoryGrokPatternService(ClusterEventBus clusterBus) {
        this.clusterBus = clusterBus;
    }

    @Override
    public GrokPattern load(String patternId) throws NotFoundException {
        final GrokPattern pattern = store.get(patternId);
        if (pattern == null) {
            throw new NotFoundException("Couldn't find Grok pattern with ID " + patternId);
        }
        return pattern;
    }

    @Override
    public Optional<GrokPattern> loadByName(String name) {
        return store.values().stream()
                .filter(pattern -> pattern.name().equals(name))
                .findAny();
    }

    @Override
    public Set<GrokPattern> bulkLoad(Collection<String> patternIds) {
        return patternIds.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<GrokPattern> loadAll() {
        return Sets.newHashSet(store.values());
    }


    @Override
    public GrokPattern update(GrokPattern pattern) throws ValidationException {
        return save(pattern);
    }

    @Override
    public GrokPattern save(GrokPattern pattern) throws ValidationException {
        try {
            if (!validate(pattern)) {
                throw new ValidationException("Pattern " + pattern.name() + " invalid.");
            }
        } catch (GrokException | PatternSyntaxException e) {
            throw new ValidationException("Invalid pattern " + pattern + "\n" + e.getMessage());
        }

        GrokPattern toSave;
        if (pattern.id() == null) {
            toSave = pattern.toBuilder().id(createId()).build();
        } else {
            toSave = pattern;
        }
        store.put(toSave.id(), toSave);
        clusterBus.post(GrokPatternsUpdatedEvent.create(ImmutableSet.of(toSave.name())));

        return toSave;
    }

    /**
     * Like #save but swallows the exception and returns null.
     *
     * @param pattern pattern to save
     * @return the saved pattern or null
     */
    private GrokPattern uncheckedSave(GrokPattern pattern) {
        try {
            return save(pattern);
        } catch (ValidationException e) {
            return null;
        }
    }

    @Override
    public List<GrokPattern> saveAll(Collection<GrokPattern> patterns,
                                     boolean replace) throws ValidationException {
        try {
            if (!validateAll(patterns)) {
                throw new ValidationException("Patterns invalid.");
            }
        } catch (GrokException | PatternSyntaxException e) {
            throw new ValidationException("Invalid patterns.\n" + e.getMessage());
        }

        if (replace) {
            deleteAll();
        }

        final List<GrokPattern> grokPatterns = patterns.stream()
                .map(this::uncheckedSave)
                .collect(Collectors.toList());

        final Set<String> patternNames = grokPatterns.stream()
                .map(GrokPattern::name)
                .collect(Collectors.toSet());

        if (!patternNames.isEmpty()) {
            clusterBus.post(GrokPatternsUpdatedEvent.create(patternNames));
        }

        return grokPatterns;
    }

    @Override
    public Map<String, Object> match(GrokPattern pattern, String sampleData) throws GrokException {
        final Set<GrokPattern> patterns = loadAll();
        final GrokCompiler grokCompiler = GrokCompiler.newInstance();
        for (GrokPattern storedPattern : patterns) {
            grokCompiler.register(storedPattern.name(), storedPattern.pattern());
        }
        grokCompiler.register(pattern.name(), pattern.pattern());
        Grok grok = grokCompiler.compile("%{" + pattern.name() + "}");
        return grok.match(sampleData).captureFlattened();
    }

    @Override
    public boolean validate(GrokPattern pattern) throws GrokException {
        final Set<GrokPattern> patterns = loadAll();
        final boolean fieldsMissing = Strings.isNullOrEmpty(pattern.name()) || Strings.isNullOrEmpty(pattern.pattern());
        final GrokCompiler grokCompiler = GrokCompiler.newInstance();
        for (GrokPattern storedPattern : patterns) {
            grokCompiler.register(storedPattern.name(), storedPattern.pattern());
        }
        grokCompiler.register(pattern.name(), pattern.pattern());
        grokCompiler.compile("%{" + pattern.name() + "}");
        return !fieldsMissing;
    }

    @Override
    public boolean validateAll(Collection<GrokPattern> newPatterns) throws GrokException {
        final Set<GrokPattern> patterns = loadAll();
        final GrokCompiler grokCompiler = GrokCompiler.newInstance();

        for (GrokPattern newPattern : newPatterns) {
            final boolean fieldsMissing = Strings.isNullOrEmpty(newPattern.name()) || Strings.isNullOrEmpty(newPattern.pattern());
            if (fieldsMissing) {
                return false;
            }
            grokCompiler.register(newPattern.name(), newPattern.pattern());
        }
        for (GrokPattern storedPattern : patterns) {
            grokCompiler.register(storedPattern.name(), storedPattern.pattern());
        }
        for (GrokPattern newPattern : newPatterns) {
            grokCompiler.compile("%{" + newPattern.name() + "}");
        }
        return true;
    }

    @Override
    public int delete(String patternId) {
        final GrokPattern grokPattern = store.remove(patternId);
        if (grokPattern != null) {
            clusterBus.post(GrokPatternsDeletedEvent.create(ImmutableSet.of(grokPattern.name())));
        }

        return grokPattern == null ? 0 : 1;
    }

    @Override
    public int deleteAll() {
        final Set<String> patternNames = store.values().stream()
                .map(GrokPattern::name)
                .collect(Collectors.toSet());

        if (!patternNames.isEmpty()) {
            store.clear();
            clusterBus.post(GrokPatternsDeletedEvent.create(patternNames));
        }

        return patternNames.size();
    }

    private String createId() {
        return String.valueOf(idGen.incrementAndGet());
    }

}
