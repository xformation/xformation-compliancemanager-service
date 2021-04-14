/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;

import com.synectiks.process.common.plugins.views.search.export.ExportException;
import com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils;
import com.synectiks.process.server.indexer.ElasticsearchException;

import javax.inject.Inject;

import static com.synectiks.process.common.storage.elasticsearch6.jest.JestUtils.checkForFailedShards;

import java.util.Optional;
import java.util.function.Supplier;

public class JestWrapper {
    private final JestClient jestClient;

    @Inject
    public JestWrapper(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    public <T extends JestResult> T execute(Action<T> action, Supplier<String> errorMessageSupplier) {
        final T result = JestUtils.execute(jestClient, action, errorMessageSupplier);
        Optional<ElasticsearchException> elasticsearchException = checkForFailedShards(result);
        if (elasticsearchException.isPresent()) {
            throw new ExportException(errorMessageSupplier.get(), elasticsearchException.get());
        }
        return result;
    }
}
