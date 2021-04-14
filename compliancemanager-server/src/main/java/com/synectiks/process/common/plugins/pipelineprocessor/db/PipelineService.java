/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db;

import java.util.Collection;

import com.synectiks.process.server.database.NotFoundException;

public interface PipelineService {
    PipelineDao save(PipelineDao pipeline);

    PipelineDao load(String id) throws NotFoundException;

    PipelineDao loadByName(String name) throws NotFoundException;

    Collection<PipelineDao> loadAll();

    void delete(String id);
}
