/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import org.junit.jupiter.api.Test;

import com.synectiks.process.common.plugins.views.search.export.MessagesRequest;

import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_CHUNK_SIZE;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_FIELDS;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_QUERY;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_STREAMS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MessagesRequestTest {
    @Test
    void fillsDefaults() {
        MessagesRequest defaultRequest = MessagesRequest.builder().build();

        assertAll("Should fill every empty field with default",
                () -> assertThat(defaultRequest.timeRange()).isNotNull(),
                () -> assertThat(defaultRequest.queryString()).isEqualTo(DEFAULT_QUERY),
                () -> assertThat(defaultRequest.streams()).isEqualTo(DEFAULT_STREAMS),
                () -> assertThat(defaultRequest.fieldsInOrder()).isEqualTo(DEFAULT_FIELDS),
                () -> assertThat(defaultRequest.chunkSize()).isEqualTo(DEFAULT_CHUNK_SIZE));
    }
}
