/*
 * */
package com.synectiks.process.server.indexer;

import com.synectiks.process.server.plugin.database.Persisted;

public interface IndexFailure extends Persisted {
    String letterId();
}
