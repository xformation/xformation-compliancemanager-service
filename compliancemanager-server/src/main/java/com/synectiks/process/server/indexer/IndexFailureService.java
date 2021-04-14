/*
 * */
package com.synectiks.process.server.indexer;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.database.PersistedService;

import java.util.List;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface IndexFailureService extends PersistedService {
    List<IndexFailure> all(int limit, int offset);

    long countSince(DateTime since);

    long totalCount();
}
