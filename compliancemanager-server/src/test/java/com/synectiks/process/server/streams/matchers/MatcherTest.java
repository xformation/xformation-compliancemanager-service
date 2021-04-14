/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.streams.InvalidStreamRuleTypeException;
import com.synectiks.process.server.streams.StreamRuleMatcherFactory;
import com.synectiks.process.server.streams.matchers.StreamRuleMatcher;

import org.bson.types.ObjectId;

import java.util.Map;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class MatcherTest {
    protected StreamRule getSampleRule() {
        Map<String, Object> mongoRule = Maps.newHashMap();
        mongoRule.put("_id", new ObjectId());
        mongoRule.put("field", "something");

        return new StreamRuleMock(mongoRule);
    }

    protected Message getSampleMessage() {
        return new Message("foo", "bar", Tools.nowUTC());
    }

    protected StreamRuleMatcher getMatcher(StreamRule rule) {
        StreamRuleMatcher matcher;
        try {
            matcher = StreamRuleMatcherFactory.build(rule.getType());
        } catch (InvalidStreamRuleTypeException e) {
            return null;
        }

        return matcher;
    }
}
