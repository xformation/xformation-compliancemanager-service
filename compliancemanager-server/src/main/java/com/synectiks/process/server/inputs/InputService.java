/*
 * */
package com.synectiks.process.server.inputs;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.PersistedService;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.shared.inputs.NoSuchInputTypeException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InputService extends PersistedService {
    List<Input> all();

    List<Input> allOfThisNode(String nodeId);

    Input create(String id, Map<String, Object> fields);

    Input create(Map<String, Object> fields);

    String update(Input model) throws ValidationException;

    Input find(String id) throws NotFoundException;

    Set<Input> findByIds(Collection<String> ids);

    Input findForThisNode(String nodeId, String id) throws NotFoundException;

    Input findForThisNodeOrGlobal(String nodeId, String id) throws NotFoundException;

    /**
     * @return the total number of inputs in the cluster (including global inputs).
     */
    long totalCount();

    /**
     * @return the number of global inputs in the cluster.
     */
    long globalCount();

    /**
     * @return the number of node-specific inputs in the cluster.
     */
    long localCount();

    /**
     * @return the total number of inputs in the cluster grouped by type.
     */
    Map<String, Long> totalCountByType();

    /**
     * @param nodeId the node ID to query
     * @return the number of inputs on the specified node
     */
    long localCountForNode(String nodeId);

    /**
     * @param nodeId the node ID to query
     * @return the number of inputs on the specified node (including global inputs)
     */
    long totalCountForNode(String nodeId);

    /**
     * @return the total number of extractors in the cluster (including global inputs).
     */
    long totalExtractorCount();

    /**
     * @return the total number of extractors in the cluster (including global inputs) grouped by type.
     */
    Map<Extractor.Type, Long> totalExtractorCountByType();

    void addExtractor(Input input, Extractor extractor) throws ValidationException;

    void addStaticField(Input input, String key, String value) throws ValidationException;

    List<Extractor> getExtractors(Input input);

    Extractor getExtractor(Input input, String extractorId) throws NotFoundException;

    void updateExtractor(Input input, Extractor extractor) throws ValidationException;

    void removeExtractor(Input input, String extractorId);

    void removeStaticField(Input input, String key);

    MessageInput getMessageInput(Input io) throws NoSuchInputTypeException;

    List<Map.Entry<String, String>> getStaticFields(Input input);
}
