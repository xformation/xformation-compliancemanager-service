/*
 * */
package com.synectiks.process.server.audit.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditEventType;

public interface AuditEventFormatter {
    FormattedAuditEvent format(AuditActor actor, AuditEventType type, JsonNode jsonNodeContext);
}
