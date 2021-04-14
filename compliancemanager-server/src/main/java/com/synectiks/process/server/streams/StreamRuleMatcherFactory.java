/*
 * */
package com.synectiks.process.server.streams;

import com.synectiks.process.server.plugin.streams.StreamRuleType;
import com.synectiks.process.server.streams.matchers.AlwaysMatcher;
import com.synectiks.process.server.streams.matchers.ContainsMatcher;
import com.synectiks.process.server.streams.matchers.ExactMatcher;
import com.synectiks.process.server.streams.matchers.FieldPresenceMatcher;
import com.synectiks.process.server.streams.matchers.GreaterMatcher;
import com.synectiks.process.server.streams.matchers.InputMatcher;
import com.synectiks.process.server.streams.matchers.RegexMatcher;
import com.synectiks.process.server.streams.matchers.SmallerMatcher;
import com.synectiks.process.server.streams.matchers.StreamRuleMatcher;

public class StreamRuleMatcherFactory {
    public static StreamRuleMatcher build(StreamRuleType ruleType) throws InvalidStreamRuleTypeException {
        switch (ruleType) {
            case EXACT:
                return new ExactMatcher();
            case REGEX:
                return new RegexMatcher();
            case GREATER:
                return new GreaterMatcher();
            case SMALLER:
                return new SmallerMatcher();
            case PRESENCE:
                return new FieldPresenceMatcher();
            case CONTAINS:
                return new ContainsMatcher();
            case ALWAYS_MATCH:
                return new AlwaysMatcher();
            case MATCH_INPUT:
                return new InputMatcher();
            default:
                throw new InvalidStreamRuleTypeException();
        }
    }
}
