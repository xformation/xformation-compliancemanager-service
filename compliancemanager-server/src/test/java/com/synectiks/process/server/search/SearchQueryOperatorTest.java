/*
 * */
package com.synectiks.process.server.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.synectiks.process.server.search.SearchQueryOperator;

class SearchQueryOperatorTest {
    @Nested
    class RegexpTest {
        private SearchQueryOperator.Regexp operator;

        @BeforeEach
        void setUp() {
            this.operator = new SearchQueryOperator.Regexp();
        }

        @Test
        void withRegexpMetaCharacters() {
            // Using regexp meta characters should now throw an exception
            operator.buildQuery("hello", "foo\\");
        }
    }
}
