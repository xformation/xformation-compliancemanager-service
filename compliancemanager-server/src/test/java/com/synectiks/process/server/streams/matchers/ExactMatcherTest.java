/*
 * */
package com.synectiks.process.server.streams.matchers;

import org.junit.Test;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.plugin.streams.StreamRuleType;
import com.synectiks.process.server.streams.matchers.ExactMatcher;
import com.synectiks.process.server.streams.matchers.StreamRuleMatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExactMatcherTest extends MatcherTest {

    @Test
    public void testSuccessfulMatch() {
        StreamRule rule = getSampleRule();

        Message msg = getSampleMessage();
        msg.addField("something", "foo");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testMissedMatch() {
        StreamRule rule = getSampleRule();

        Message msg = getSampleMessage();
        msg.addField("something", "nonono");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testInvertedMatch() {
        StreamRule rule = getSampleRule();
        rule.setInverted(true);

        Message msg = getSampleMessage();
        msg.addField("something", "nonono");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testNonExistantField() {
        StreamRule rule = getSampleRule();

        Message msg = getSampleMessage();
        msg.addField("someother", "foo");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testNonExistantFieldInverted() {
        StreamRule rule = getSampleRule();
        rule.setInverted(true);

        Message msg = getSampleMessage();
        msg.addField("someother", "foo");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testNullFieldShouldNotMatch() {
        final String fieldName = "nullfield";
        final StreamRule rule = getSampleRule();
        rule.setField(fieldName);

        final Message msg = getSampleMessage();
        msg.addField(fieldName, null);

        final StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testInvertedNullFieldShouldMatch() {
        final String fieldName = "nullfield";
        final StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setInverted(true);

        final Message msg = getSampleMessage();
        msg.addField(fieldName, null);

        final StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Override
    protected StreamRule getSampleRule() {
        StreamRule rule = super.getSampleRule();
        rule.setType(StreamRuleType.EXACT);
        rule.setValue("foo");

        return rule;
    }

    @Override
    protected StreamRuleMatcher getMatcher(StreamRule rule) {
        StreamRuleMatcher matcher = super.getMatcher(rule);

        assertEquals(matcher.getClass(), ExactMatcher.class);

        return matcher;
    }

}
