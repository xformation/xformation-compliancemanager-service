/*
 * */
package com.synectiks.process.server.indexer;

import java.util.Collections;
import java.util.List;

public class FieldTypeException extends ElasticsearchException {
    public FieldTypeException(String message, String reason) {
        this(message, Collections.singletonList(reason));
    }

    public FieldTypeException(String message, List<String> errorDetails) {
        super(message, errorDetails);
    }
}
