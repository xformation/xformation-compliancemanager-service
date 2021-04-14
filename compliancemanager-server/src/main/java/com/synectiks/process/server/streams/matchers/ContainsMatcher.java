/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;

public class ContainsMatcher implements StreamRuleMatcher {
    @Override
    public boolean match(Message msg, StreamRule rule) {
        final boolean inverted = rule.getInverted();
        final Object field = msg.getField(rule.getField());

        if (field != null) {
            final String value = field.toString();
            return inverted ^ value.contains(rule.getValue());
        } else {
            return inverted;
        }
    }
}
