/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;


/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public interface StreamRuleMatcher {

    boolean match(Message msg, StreamRule rule);

}