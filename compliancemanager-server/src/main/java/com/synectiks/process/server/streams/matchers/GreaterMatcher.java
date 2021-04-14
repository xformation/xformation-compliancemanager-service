/*
 * */
package com.synectiks.process.server.streams.matchers;

import static com.synectiks.process.server.plugin.Tools.getDouble;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;

/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class GreaterMatcher implements StreamRuleMatcher {

	@Override
	public boolean match(Message msg, StreamRule rule) {
        Double msgVal = getDouble(msg.getField(rule.getField()));
        if (msgVal == null) {
            return false;
        }

        Double ruleVal = getDouble(rule.getValue());
        if (ruleVal == null) {
            return false;
        }

        return rule.getInverted() ^ (msgVal > ruleVal);
	}

}
