/*
 * */
package com.synectiks.process.server.search;

public class SearchQueryOperators {
    public static final SearchQueryOperator EQUALS = new SearchQueryOperator.Equals();
    public static final SearchQueryOperator GREATER = new SearchQueryOperator.Greater();
    public static final SearchQueryOperator GREATER_EQUALS = new SearchQueryOperator.GreaterEquals();
    public static final SearchQueryOperator LESS = new SearchQueryOperator.Less();
    public static final SearchQueryOperator LESS_EQUALS = new SearchQueryOperator.LessEquals();
    public static final SearchQueryOperator REGEXP = new SearchQueryOperator.Regexp();
}
