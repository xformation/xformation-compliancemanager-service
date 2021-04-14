/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;

/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class ExactMatcher implements StreamRuleMatcher {

	@Override
	public boolean match(Message msg, StreamRule rule) {
        if (msg.getField(rule.getField()) == null) {
            return rule.getInverted();
        }

		final String value = msg.getField(rule.getField()).toString();

		return rule.getInverted() ^ value.trim().equals(rule.getValue());
	}

}
