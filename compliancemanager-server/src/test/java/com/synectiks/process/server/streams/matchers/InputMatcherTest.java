/*
 * */
package com.synectiks.process.server.streams.matchers;

import org.junit.Test;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.plugin.streams.StreamRuleType;
import com.synectiks.process.server.streams.matchers.StreamRuleMatcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputMatcherTest extends MatcherTest {

    @Test
    public void testSuccessfulMatch() {
        StreamRule rule = getSampleRule();
        rule.setValue("input-id-beef");

        Message msg = getSampleMessage();
        msg.addField(Message.FIELD_XFALERT_SOURCE_INPUT, "input-id-beef");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testUnsuccessfulMatch() {
        StreamRule rule = getSampleRule();
        rule.setValue("input-id-dead");

        Message msg = getSampleMessage();
        msg.addField(Message.FIELD_XFALERT_SOURCE_INPUT, "input-id-beef");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testUnsuccessfulMatchWhenMissing() {
        StreamRule rule = getSampleRule();
        rule.setValue("input-id-dead");

        Message msg = getSampleMessage();

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testSuccessfulMatchInverted() {
        StreamRule rule = getSampleRule();
        rule.setValue("input-id-beef");
        rule.setInverted(true);

        Message msg = getSampleMessage();
        msg.addField(Message.FIELD_XFALERT_SOURCE_INPUT, "input-id-beef");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testUnsuccessfulMatchInverted() {
        StreamRule rule = getSampleRule();
        rule.setValue("input-id-dead");
        rule.setInverted(true);

        Message msg = getSampleMessage();
        msg.addField(Message.FIELD_XFALERT_SOURCE_INPUT, "input-id-beef");

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testUnsuccessfulMatchWhenMissingInverted() {
        StreamRule rule = getSampleRule();
        rule.setValue("input-id-dead");
        rule.setInverted(true);

        Message msg = getSampleMessage();

        StreamRuleMatcher matcher = getMatcher(rule);
        assertTrue(matcher.match(msg, rule));
    }

    @Override
    protected StreamRule getSampleRule() {
        StreamRule rule = super.getSampleRule();
        rule.setType(StreamRuleType.MATCH_INPUT);

        return rule;
    }

}
