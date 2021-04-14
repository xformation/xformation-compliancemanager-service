/*
 * */
package com.synectiks.process.server.streams.matchers;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.plugin.streams.StreamRuleType;
import com.synectiks.process.server.streams.matchers.ContainsMatcher;
import com.synectiks.process.server.streams.matchers.StreamRuleMatcher;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContainsMatcherTest extends MatcherTest {
    private StreamRule rule;
    private Message msg;

    @Before
    public void setUp() {
        rule = getSampleRule();
        msg = getSampleMessage();
    }

    @Test
    public void testSuccessfulMatch() {
        msg.addField("something", "foobar");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testMissedMatch() {
        msg.addField("something", "nonono");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testInvertedMatch() {
        rule.setInverted(true);

        msg.addField("something", "nonono");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testNonExistentField() {
        msg.addField("someother", "hello foo");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testNonExistentFieldInverted() {
        rule.setInverted(true);

        msg.addField("someother", "hello foo");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testNullFieldShouldNotMatch() {
        final String fieldName = "nullfield";
        rule.setField(fieldName);

        msg.addField(fieldName, null);

        final StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testInvertedNullFieldShouldMatch() {
        final String fieldName = "nullfield";
        rule.setField(fieldName);
        rule.setInverted(true);

        msg.addField(fieldName, null);

        final StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testSuccessfulMatchInArray() {
        msg.addField("something", Collections.singleton("foobar"));

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Override
    protected StreamRule getSampleRule() {
        final StreamRule rule = super.getSampleRule();
        rule.setType(StreamRuleType.CONTAINS);
        rule.setValue("foo");

        return rule;
    }

    @Override
    protected StreamRuleMatcher getMatcher(StreamRule rule) {
        final StreamRuleMatcher matcher = super.getMatcher(rule);

        assertEquals(matcher.getClass(), ContainsMatcher.class);

        return matcher;
    }
}