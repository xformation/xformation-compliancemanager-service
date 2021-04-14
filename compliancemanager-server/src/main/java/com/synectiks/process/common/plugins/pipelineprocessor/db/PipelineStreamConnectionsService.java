/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db;

import com.synectiks.process.common.plugins.pipelineprocessor.rest.PipelineConnections;
import com.synectiks.process.server.database.NotFoundException;

import java.util.Set;

public interface PipelineStreamConnectionsService {
    PipelineConnections save(PipelineConnections connections);

    PipelineConnections load(String streamId) throws NotFoundException;

    Set<PipelineConnections> loadAll();

    Set<PipelineConnections> loadByPipelineId(String pipelineId);

    void delete(String streamId);
}
