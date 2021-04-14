/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.server.indexer.ElasticsearchException;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchTypeErrorParser {
    public static SearchTypeError parse(Query query, String searchTypeId, ElasticsearchException ex) {
        final Integer resultWindowLimit = parseResultLimit(ex);

        if (resultWindowLimit != null)
            return new ResultWindowLimitError(query, searchTypeId, resultWindowLimit, ex);

        return new SearchTypeError(query, searchTypeId, ex);
    }

    private static Integer parseResultLimit(Throwable throwable) {
        return parseResultLimit(throwable.getMessage());
    }

    private static Integer parseResultLimit(String description) {
        if (description.toLowerCase(Locale.US).contains("result window is too large")) {
            final Matcher matcher = Pattern.compile("[0-9]+").matcher(description);
            if (matcher.find())
                return Integer.parseInt(matcher.group(0));
        }
        return null;
    }

}
