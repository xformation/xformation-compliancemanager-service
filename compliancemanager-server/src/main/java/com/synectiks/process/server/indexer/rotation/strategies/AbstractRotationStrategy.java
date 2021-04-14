/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.NoTargetIndexException;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategy;
import com.synectiks.process.server.plugin.system.NodeId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static com.synectiks.process.server.audit.AuditEventTypes.ES_INDEX_ROTATION_COMPLETE;
import static java.util.Objects.requireNonNull;

public abstract class AbstractRotationStrategy implements RotationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRotationStrategy.class);

    public interface Result {
        String getDescription();
        boolean shouldRotate();
    }

    private final AuditEventSender auditEventSender;
    private final NodeId nodeId;

    public AbstractRotationStrategy(AuditEventSender auditEventSender, NodeId nodeId) {
        this.auditEventSender = requireNonNull(auditEventSender);
        this.nodeId = nodeId;
    }

    @Nullable
    protected abstract Result shouldRotate(String indexName, IndexSet indexSet);

    @Override
    public void rotate(IndexSet indexSet) {
        requireNonNull(indexSet, "indexSet must not be null");
        final String indexSetTitle = requireNonNull(indexSet.getConfig(), "Index set configuration must not be null").title();
        final String strategyName = this.getClass().getCanonicalName();
        final String indexName;
        try {
            indexName = indexSet.getNewestIndex();
        } catch (NoTargetIndexException e) {
            LOG.error("Could not find current deflector target of index set <{}>. Aborting.", indexSetTitle, e);
            return;
        }

        final Result rotate = shouldRotate(indexName, indexSet);
        if (rotate == null) {
            LOG.error("Cannot perform rotation of index <{}> in index set <{}> with strategy <{}> at this moment", indexName, indexSetTitle, strategyName);
            return;
        }
        LOG.debug("Rotation strategy result: {}", rotate.getDescription());
        if (rotate.shouldRotate()) {
            LOG.info("Deflector index <{}> (index set <{}>) should be rotated, Pointing deflector to new index now!", indexSetTitle, indexName);
            indexSet.cycle();
            auditEventSender.success(AuditActor.system(nodeId), ES_INDEX_ROTATION_COMPLETE, ImmutableMap.of(
                    "index_name", indexName,
                    "rotation_strategy", strategyName
            ));
        } else {
            LOG.debug("Deflector index <{}> should not be rotated. Not doing anything.", indexName);
        }
    }
}
