/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.plugin.system.NodeId;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;

public class SizeBasedRotationStrategy extends AbstractRotationStrategy {
    private final Indices indices;

    @Inject
    public SizeBasedRotationStrategy(Indices indices,
                                     NodeId nodeId,
                                     AuditEventSender auditEventSender) {
        super(auditEventSender, nodeId);
        this.indices = indices;
    }

    @Override
    public Class<? extends RotationStrategyConfig> configurationClass() {
        return SizeBasedRotationStrategyConfig.class;
    }

    @Override
    public RotationStrategyConfig defaultConfiguration() {
        return SizeBasedRotationStrategyConfig.createDefault();
    }

    @Nullable
    @Override
    protected Result shouldRotate(final String index, IndexSet indexSet) {
        if (!(indexSet.getConfig().rotationStrategy() instanceof SizeBasedRotationStrategyConfig)) {
            throw new IllegalStateException("Invalid rotation strategy config <" + indexSet.getConfig().rotationStrategy().getClass().getCanonicalName() + "> for index set <" + indexSet.getConfig().id() + ">");
        }

        final SizeBasedRotationStrategyConfig config = (SizeBasedRotationStrategyConfig) indexSet.getConfig().rotationStrategy();

        final Optional<Long> storeSizeInBytes = indices.getStoreSizeInBytes(index);
        if (!storeSizeInBytes.isPresent()) {
            return null;
        }

        final long sizeInBytes = storeSizeInBytes.get();
        final boolean shouldRotate = sizeInBytes > config.maxSize();

        return new Result() {
            public final MessageFormat ROTATE = new MessageFormat("Storage size for index <{0}> is {1} bytes, exceeding the maximum of {2} bytes. Rotating index.", Locale.ENGLISH);
            public final MessageFormat NOT_ROTATE = new MessageFormat("Storage size for index <{0}> is {1} bytes, below the maximum of {2} bytes. Not doing anything.", Locale.ENGLISH);

            @Override
            public String getDescription() {
                MessageFormat format = shouldRotate() ? ROTATE : NOT_ROTATE;
                return format.format(new Object[]{
                        index,
                        sizeInBytes,
                        config.maxSize()
                });
            }

            @Override
            public boolean shouldRotate() {
                return shouldRotate;
            }
        };
    }
}
