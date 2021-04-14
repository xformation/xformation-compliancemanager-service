/*
 * */
package com.synectiks.process.server.indexer.results;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class HighlightParser {
    private static final String startToken = "<em>";
    private static final String endToken = "</em>";
    private static final Pattern highlightPattern = Pattern.compile(startToken + "(.*?)" + endToken);
    private static final Integer startTokenLength = startToken.length();
    private static final Integer endTokenLength = endToken.length();

    private HighlightParser() {}

    static Multimap<String, Range<Integer>> extractHighlightRanges(Map<String, List<String>> highlight) {
        if (highlight == null || highlight.isEmpty()) {
            return ImmutableListMultimap.of();
        }
        final ImmutableListMultimap.Builder<String, Range<Integer>> builder = ImmutableListMultimap.builder();
        highlight.forEach((key, value) -> extractRange(value).forEach(range -> builder.put(key, range)));
        return builder.build();
    }

    private static Set<Range<Integer>> extractRange(List<String> highlights) {
        final ImmutableSet.Builder<Range<Integer>> builder = ImmutableSet.builder();
        highlights.forEach(highlight -> {
            final Matcher matcher = highlightPattern.matcher(highlight);
            Integer count = -1;
            while (matcher.find()) {
                count++;
                final Integer start = matcher.start() - count * (startTokenLength + endTokenLength);
                final Integer end = start + (matcher.end(1) - matcher.start(1));
                builder.add(Range.closed(start, end));
            }
        });
        return builder.build();
    }
}
