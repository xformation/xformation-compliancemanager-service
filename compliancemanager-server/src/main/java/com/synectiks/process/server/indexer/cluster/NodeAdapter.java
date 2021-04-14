/*
 * */
package com.synectiks.process.server.indexer.cluster;

import java.util.Optional;

public interface NodeAdapter {
    Optional<String> version();
}
