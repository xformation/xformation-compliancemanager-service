/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.grok.GrokPattern;
import com.synectiks.process.server.grok.InMemoryGrokPatternService;
import com.synectiks.process.server.rest.resources.tools.GrokTesterResource;
import com.synectiks.process.server.rest.resources.tools.responses.GrokTesterResponse;
import com.synectiks.process.server.shared.SuppressForbidden;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import java.util.Collections;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class GrokTesterResourceTest {
    static {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    private GrokTesterResource resource;

    @Before
    @SuppressForbidden("Using Executors.newSingleThreadExecutor() is okay in tests")
    public void setUp() throws Exception {
        final ClusterEventBus clusterEventBus = new ClusterEventBus("cluster-event-bus", Executors.newSingleThreadExecutor());
        final InMemoryGrokPatternService grokPatternService = new InMemoryGrokPatternService(clusterEventBus);
        grokPatternService.save(GrokPattern.create("NUMBER", "[0-9]+"));
        resource = new GrokTesterResource(grokPatternService);
    }

    @Test
    public void testGrokWithValidPatternAndMatch() {
        final GrokTesterResponse response = resource.grokTest("%{NUMBER}", "abc 1234", false);
        assertThat(response.matched()).isTrue();
        assertThat(response.pattern()).isEqualTo("%{NUMBER}");
        assertThat(response.string()).isEqualTo("abc 1234");
        assertThat(response.matches()).containsOnly(GrokTesterResponse.Match.create("NUMBER", "1234"));
        assertThat(response.errorMessage()).isNullOrEmpty();
    }

    @Test
    public void testGrokWithValidPatternAndNoMatch() {
        final GrokTesterResponse response = resource.grokTest("%{NUMBER}", "abc def", false);
        assertThat(response.matched()).isFalse();
        assertThat(response.pattern()).isEqualTo("%{NUMBER}");
        assertThat(response.string()).isEqualTo("abc def");
        assertThat(response.matches()).isEmpty();
        assertThat(response.errorMessage()).isNullOrEmpty();
    }

    @Test
    public void testGrokWithInvalidPattern() {
        final GrokTesterResponse response = resource.grokTest("%{NUMBER", "abc 1234", false);
        assertThat(response.matched()).isFalse();
        assertThat(response.pattern()).isEqualTo("%{NUMBER");
        assertThat(response.string()).isEqualTo("abc 1234");
        assertThat(response.errorMessage()).startsWith("Illegal repetition near index 0");
    }

    @Test
    public void testGrokWithMissingPattern() {
        final GrokTesterResponse response = resource.grokTest("%{FOOBAR} %{NUMBER}", "abc 1234", false);
        assertThat(response.matched()).isFalse();
        assertThat(response.pattern()).isEqualTo("%{FOOBAR} %{NUMBER}");
        assertThat(response.string()).isEqualTo("abc 1234");
        assertThat(response.errorMessage()).isEqualTo("No definition for key 'FOOBAR' found, aborting");
    }

    @Test
    public void testGrokWithEmptyPattern() {
        final GrokTesterResponse response = resource.grokTest("", "abc 1234", false);
        assertThat(response.matched()).isFalse();
        assertThat(response.pattern()).isEqualTo("");
        assertThat(response.string()).isEqualTo("abc 1234");
        assertThat(response.errorMessage()).isEqualTo("{pattern} should not be empty or null");
    }

    @Test
    public void testGrokWithEmptyTestString() {
        final GrokTesterResponse response = resource.grokTest("%{NUMBER}", "", false);
        assertThat(response.matched()).isFalse();
        assertThat(response.pattern()).isEqualTo("%{NUMBER}");
        assertThat(response.string()).isEqualTo("");
        assertThat(response.errorMessage()).isNullOrEmpty();
    }
}