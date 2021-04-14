/*
 * */
package com.synectiks.process.server.inputs.extractors;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.inputs.extractors.RegexReplaceExtractor;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexReplaceExtractorTest extends AbstractExtractorTest {
    @Test(expected = ConfigurationException.class)
    public void testConstructorWithMissingRegex() throws Exception {
        new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of(),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
    }

    @Test(expected = ConfigurationException.class)
    public void testConstructorWithNonStringRegex() throws Exception {
        new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", 0L),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
    }

    @Test(expected = ConfigurationException.class)
    public void testConstructorWithNonStringReplacement() throws Exception {
        new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "NO-MATCH", "replacement", 0L),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
    }

    @Test
    public void testReplacementWithNoMatchAndDefaultReplacement() throws Exception {
        final Message message = new Message("Test", "source", Tools.nowUTC());
        final RegexReplaceExtractor extractor = new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "NO-MATCH"),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
        extractor.runExtractor(message);

        assertThat(message.getMessage()).isEqualTo("Test");
    }

    @Test
    public void testReplacementWithOnePlaceholder() throws Exception {
        final Message message = new Message("Test Foobar", "source", Tools.nowUTC());
        final RegexReplaceExtractor extractor = new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "Test (\\w+)"),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
        extractor.runExtractor(message);

        assertThat(message.getMessage()).isEqualTo("Foobar");
    }

    @Test(expected = RuntimeException.class)
    public void testReplacementWithTooManyPlaceholders() throws Exception {
        final Message message = new Message("Foobar 123", "source", Tools.nowUTC());
        final RegexReplaceExtractor extractor = new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "Foobar (\\d+)", "replacement", "$1 $2"),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
        extractor.runExtractor(message);
    }

    @Test
    public void testReplacementWithCustomReplacement() throws Exception {
        final Message message = new Message("Foobar 123", "source", Tools.nowUTC());
        final RegexReplaceExtractor extractor = new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "(Foobar) (\\d+)", "replacement", "$2/$1"),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
        extractor.runExtractor(message);

        assertThat(message.getMessage()).isEqualTo("123/Foobar");
    }

    @Test
    public void testReplacementWithReplaceAll() throws Exception {
        final Message message = new Message("Foobar 123 Foobaz 456", "source", Tools.nowUTC());
        final RegexReplaceExtractor extractor = new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "(\\w+) (\\d+)", "replacement", "$2/$1", "replace_all", true),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
        extractor.runExtractor(message);

        assertThat(message.getMessage()).isEqualTo("123/Foobar 456/Foobaz");
    }

    @Test
    public void testReplacementWithoutReplaceAll() throws Exception {
        final Message message = new Message("Foobar 123 Foobaz 456", "source", Tools.nowUTC());
        final RegexReplaceExtractor extractor = new RegexReplaceExtractor(
                metricRegistry,
                "id",
                "title",
                0L,
                Extractor.CursorStrategy.COPY,
                "message",
                "message",
                ImmutableMap.<String, Object>of("regex", "(\\w+) (\\d+)", "replacement", "$2/$1", "replace_all", false),
                "user",
                Collections.<Converter>emptyList(),
                Extractor.ConditionType.NONE,
                null);
        extractor.runExtractor(message);

        assertThat(message.getMessage()).isEqualTo("123/Foobar Foobaz 456");
    }
}
