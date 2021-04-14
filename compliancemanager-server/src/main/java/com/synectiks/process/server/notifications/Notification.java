/*
 * */
package com.synectiks.process.server.notifications;

import org.joda.time.DateTime;

import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.plugin.database.Persisted;

public interface Notification extends Persisted {
    Notification addType(Type type);

    Notification addTimestamp(DateTime timestamp);

    Notification addSeverity(Severity severity);

    Notification addNode(Node node);

    DateTime getTimestamp();

    Type getType();

    Severity getSeverity();

    String getNodeId();

    Notification addDetail(String key, Object value);

    Object getDetail(String key);

    Notification addNode(String nodeId);

    enum Type {
        DEFLECTOR_EXISTS_AS_INDEX,
        MULTI_MASTER,
        NO_MASTER,
        ES_OPEN_FILES,
        ES_CLUSTER_RED,
        ES_UNAVAILABLE,
        NO_INPUT_RUNNING,
        INPUT_FAILED_TO_START,
        INPUT_FAILURE_SHUTDOWN,
        CHECK_SERVER_CLOCKS,
        OUTDATED_VERSION,
        EMAIL_TRANSPORT_CONFIGURATION_INVALID,
        EMAIL_TRANSPORT_FAILED,
        STREAM_PROCESSING_DISABLED,
        GC_TOO_LONG,
        JOURNAL_UTILIZATION_TOO_HIGH,
        JOURNAL_UNCOMMITTED_MESSAGES_DELETED,
        OUTPUT_DISABLED,
        OUTPUT_FAILING,
        INDEX_RANGES_RECALCULATION,
        GENERIC,
        ES_NODE_DISK_WATERMARK_LOW,
        ES_NODE_DISK_WATERMARK_HIGH,
        ES_NODE_DISK_WATERMARK_FLOOD_STAGE,
        ES_VERSION_MISMATCH,
        LEGACY_LDAP_CONFIG_MIGRATION
    }

    enum Severity {
        NORMAL, URGENT
    }
}
