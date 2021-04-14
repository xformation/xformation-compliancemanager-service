/*
 * */
package com.synectiks.process.server.streams.matchers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.utilities.InterruptibleCharSequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class RegexMatcher implements StreamRuleMatcher {
    private static final Logger LOG = LoggerFactory.getLogger(RegexMatcher.class);

    private static final long CACHESIZE = 1000;
    private static final LoadingCache<String, Pattern> patternCache = CacheBuilder.newBuilder().maximumSize(CACHESIZE).build(new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String key) throws Exception {
            return Pattern.compile(key, Pattern.DOTALL);
        }
    });

    @Override
    public boolean match(Message msg, StreamRule rule) {
        if (msg.getField(rule.getField()) == null)
            return rule.getInverted();

        try {
            final Pattern pattern = patternCache.get(rule.getValue());
            final CharSequence charSequence = new InterruptibleCharSequence(msg.getField(rule.getField()).toString());
            return rule.getInverted() ^ pattern.matcher(charSequence).find();
        } catch (ExecutionException e) {
            LOG.error("Unable to get pattern from regex cache: ", e);
        }

        return false;
    }

}
