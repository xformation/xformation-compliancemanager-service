/*
 * */
package com.synectiks.process.server.grok;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.grok.GrokPattern;
import com.synectiks.process.server.grok.GrokPatternRegistry;
import com.synectiks.process.server.grok.GrokPatternService;
import com.synectiks.process.server.grok.GrokPatternsUpdatedEvent;

import com.synectiks.process.server.grok.krakens.Grok;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GrokPatternRegistryTest {
    private static final GrokPattern GROK_PATTERN = GrokPattern.create("TESTNUM", "[0-9]+");
    private static final Set<GrokPattern> GROK_PATTERNS = Collections.singleton(GROK_PATTERN);

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private GrokPatternRegistry grokPatternRegistry;
    private EventBus eventBus;
    @Mock
    private GrokPatternService grokPatternService;

    @Before
    public void setUp() {
        eventBus = new EventBus("Test");
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("updater-%d").build());
        when(grokPatternService.loadAll()).thenReturn(GROK_PATTERNS);
        grokPatternRegistry = new GrokPatternRegistry(eventBus, grokPatternService, executor);
    }

    @Test
    public void grokPatternsChanged() {
        final Set<GrokPattern> newPatterns = Collections.singleton(GrokPattern.create("NEW_PATTERN", "\\w+"));
        when(grokPatternService.loadAll()).thenReturn(newPatterns);
        eventBus.post(GrokPatternsUpdatedEvent.create(Collections.singleton("NEW_PATTERN")));

        assertThat(grokPatternRegistry.patterns()).isEqualTo(newPatterns);
    }

    @Test
    public void cachedGrokForPattern() {
        final Grok grok = grokPatternRegistry.cachedGrokForPattern("%{TESTNUM}");
        assertThat(grok.getPatterns()).containsEntry(GROK_PATTERN.name(), GROK_PATTERN.pattern());
    }

    @Test
    public void cachedGrokForPatternThrowsRuntimeException() {
        expectedException.expectMessage("No definition for key 'EMPTY' found, aborting");
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(Matchers.any(IllegalArgumentException.class));

        final Set<GrokPattern> newPatterns = Collections.singleton(GrokPattern.create("EMPTY", ""));
        when(grokPatternService.loadAll()).thenReturn(newPatterns);
        eventBus.post(GrokPatternsUpdatedEvent.create(Collections.singleton("EMPTY")));

        grokPatternRegistry.cachedGrokForPattern("%{EMPTY}");
    }

    @Test
    public void cachedGrokForPatternWithNamedCaptureOnly() {
        final Grok grok = grokPatternRegistry.cachedGrokForPattern("%{TESTNUM}", true);
        assertThat(grok.getPatterns()).containsEntry(GROK_PATTERN.name(), GROK_PATTERN.pattern());
    }

    @Test
    public void cachedGrokForPatternWithNamedCaptureOnlyThrowsRuntimeException() {
        expectedException.expectMessage("No definition for key 'EMPTY' found, aborting");
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(Matchers.any(IllegalArgumentException.class));

        final Set<GrokPattern> newPatterns = Collections.singleton(GrokPattern.create("EMPTY", ""));
        when(grokPatternService.loadAll()).thenReturn(newPatterns);
        eventBus.post(GrokPatternsUpdatedEvent.create(Collections.singleton("EMPTY")));

        grokPatternRegistry.cachedGrokForPattern("%{EMPTY}", true);
    }

    @Test
    public void patterns() {
        assertThat(grokPatternRegistry.patterns()).isEqualTo(GROK_PATTERNS);
    }
}