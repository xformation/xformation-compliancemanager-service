/*
 * */
package com.synectiks.process.server.audit.formatter;

import java.util.Map;

import com.synectiks.process.server.audit.AuditActor;

public interface FormattedAuditEvent {
    /**
     * The audit event actor as URN string.
     *
     * Use {@link AuditActor#urn()} to build the URN!
     *
     * Examples:
     *
     *    {@code urn:alertmanager:user:jane}
     *    {@code urn:alertmanager:node:28164cbe-4ad9-4c9c-a76e-088655aa7889}
     *
     * @return the actor URN
     */
    String actorUrn();

    /**
     * The audit event namespace.
     *
     * Each plugin should have its own, unique namespace. The alertmanager server namespace is {@code server}.
     *
     * @return namespace string
     */
    String namespace();

    /**
     * The audit event object as URN.
     *
     * Examples:
     *
     *   {@code urn:alertmanager:dashboard:56f2fdefa0275b357744230c:widget:57ab37cc67d0cb54582d43a0}
     *   {@code urn:alertmanager:message_input:56f2fdefa0275b357744230c}
     *   {@code urn:alertmanager:pipeline-rule:57ab37cc67d0cb54582d43a0}
     *
     * @return the object URN
     */
    String objectUrn();

    /**
     * The audit event action.
     *
     * A simple string that identifies the action for the object.
     *
     * Examples:
     *
     *   {@code create}
     *   {@code delete}
     *   {@code update}
     *   {@code start}
     *   {@code stop}
     *
     * @return the action
     */
    String action();

    /**
     * The message template string that will be used to present the audit event to humans.
     *
     * All data in {@link #attributes()} as well as the following fields can be used as variables.
     *
     * <ul>
     *     <li>actor</li>
     *     <li>namespace</li>
     *     <li>object</li>
     *     <li>action</li>
     * </ul>
     *
     * Examples:
     *
     *   {@code "Message input ${input_name} created"}
     *
     * @return
     */
    String messageTemplate();

    /**
     * The audit event attributes that will be stored in the database.
     *
     * All information that is needed by the {@link #messageTemplate()} should be in here.
     *
     * Make sure you do not store any sensitive information like passwords and API tokens!
     *
     * @return the audit event attributes
     */
    Map<String, Object> attributes();
}
