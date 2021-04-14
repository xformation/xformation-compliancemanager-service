/*
 * */
package com.synectiks.process.server.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.synectiks.process.server.audit.formatter.AuditEventFormatter;
import com.synectiks.process.server.audit.formatter.FormattedAuditEvent;
import com.synectiks.process.server.plugin.PluginModule;

public class AuditBindings extends PluginModule {
    @Override
    protected void configure() {
        // Make sure there is a default binding
        auditEventSenderBinder().setDefault().to(NullAuditEventSender.class);

        addAuditEventTypes(AuditEventTypes.class);

        // Needed to avoid binding errors when there are no implementations of AuditEventFormatter.
        addAuditEventFormatter(AuditEventType.create("__ignore__:__ignore__:__ignore__"), NullAuditEventFormatter.class);
    }

    private static class NullAuditEventFormatter implements AuditEventFormatter {
        @Override
        public FormattedAuditEvent format(AuditActor actor, AuditEventType type, JsonNode jsonNodeContext) {
            return null;
        }
    }
}
