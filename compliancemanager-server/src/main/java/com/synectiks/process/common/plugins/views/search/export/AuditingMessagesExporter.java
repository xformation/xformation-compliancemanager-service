/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.plugins.views.search.events.MessagesExportEvent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class AuditingMessagesExporter implements MessagesExporter {
    private final AuditContext context;
    @SuppressWarnings("UnstableApiUsage")
    private final EventBus eventBus;
    private final MessagesExporter decoratedExporter;

    public Supplier<DateTime> startedAt = () -> DateTime.now(DateTimeZone.UTC);
    public Supplier<DateTime> finishedAt = () -> DateTime.now(DateTimeZone.UTC);

    public AuditingMessagesExporter(AuditContext context, @SuppressWarnings("UnstableApiUsage") EventBus eventBus, MessagesExporter decoratedExporter) {
        this.context = context;
        this.eventBus = eventBus;
        this.decoratedExporter = decoratedExporter;
    }

    @Override
    public void export(ExportMessagesCommand command, Consumer<SimpleMessageChunk> chunkForwarder) {
        post(MessagesExportEvent.requested(startedAt.get(), context, command));

        decoratedExporter.export(command, chunkForwarder);

        post(MessagesExportEvent.succeeded(finishedAt.get(), context, command));
    }

    private void post(Object event) {
        //noinspection UnstableApiUsage
        eventBus.post(requireNonNull(event));
    }
}
