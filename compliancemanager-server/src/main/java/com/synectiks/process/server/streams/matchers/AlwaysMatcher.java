/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;

public class AlwaysMatcher implements StreamRuleMatcher {
    @Override
    public boolean match(Message msg, StreamRule rule) {
        return true;
    }
}
