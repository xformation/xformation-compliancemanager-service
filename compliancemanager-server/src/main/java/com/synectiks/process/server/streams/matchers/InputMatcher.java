/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;

public class InputMatcher implements StreamRuleMatcher {

    @Override
    public boolean match(Message msg, StreamRule rule) {
       if(msg.getField(Message.FIELD_XFALERT_SOURCE_INPUT) == null) {
           return rule.getInverted();
       }
        final String value = msg.getField(Message.FIELD_XFALERT_SOURCE_INPUT).toString();
        return rule.getInverted() ^ value.trim().equalsIgnoreCase(rule.getValue());
    }
}
