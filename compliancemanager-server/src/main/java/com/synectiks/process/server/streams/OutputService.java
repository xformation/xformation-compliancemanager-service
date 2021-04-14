/*
 * */
package com.synectiks.process.server.streams;

import com.google.inject.ImplementedBy;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.rest.models.streams.outputs.requests.CreateOutputRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ImplementedBy(OutputServiceImpl.class)
public interface OutputService {
    Output load(String streamOutputId) throws NotFoundException;

    Set<Output> loadByIds(Collection<String> ids);

    Set<Output> loadAll();

    Output create(Output request) throws ValidationException;

    Output create(CreateOutputRequest request, String userId) throws ValidationException;

    void destroy(Output model) throws NotFoundException;

    Output update(String id, Map<String, Object> deltas);

    /**
     * @return the total number of outputs
     */
    long count();

    /**
     * @return the total number of outputs grouped by type
     */
    Map<String, Long> countByType();
}
