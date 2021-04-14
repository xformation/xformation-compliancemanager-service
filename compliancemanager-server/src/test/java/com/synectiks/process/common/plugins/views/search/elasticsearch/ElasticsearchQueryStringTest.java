/*
 * */
package com.synectiks.process.common.plugins.views.search.elasticsearch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;

import static org.assertj.core.api.Assertions.assertThat;

class ElasticsearchQueryStringTest {
    private ElasticsearchQueryString create(String queryString) {
        return ElasticsearchQueryString.builder().queryString(queryString).build();
    }

    @Test
    void concatenatingTwoEmptyStringsReturnsEmptyString() {
        assertThat(create("").concatenate(create("")).queryString()).isEmpty();
    }

    @Test
    void concatenatingNonEmptyStringWithEmptyStringReturnsFirst() {
        assertThat(create("_exists_:nf_version").concatenate(create("")).queryString()).isEqualTo("_exists_:nf_version");
    }

    @Test
    void concatenatingEmptyStringWithNonEmptyStringReturnsSecond() {
        assertThat(create("").concatenate(create("_exists_:nf_version")).queryString()).isEqualTo("_exists_:nf_version");
    }

    @Test
    void concatenatingTwoNonEmptyStringsReturnsAppendedQueryString() {
        assertThat(create("nf_bytes>200").concatenate(create("_exists_:nf_version")).queryString()).isEqualTo("nf_bytes>200 AND _exists_:nf_version");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "\n",
            "*",
            " *"
    })
    void detectsIfItsEmpty(String queryString) {
        ElasticsearchQueryString sut = ElasticsearchQueryString.builder().queryString(queryString).build();

        assertThat(sut.isEmpty()).isTrue();
    }
}
