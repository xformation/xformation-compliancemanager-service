/*
 * */
package com.synectiks.process.server.shared.buffers.processors;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.lmax.disruptor.WorkHandler;
import com.synectiks.process.server.buffers.OutputBuffer;
import com.synectiks.process.server.messageprocessors.OrderedMessageProcessors;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Messages;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.buffers.MessageEvent;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;
import com.synectiks.process.server.plugin.streams.DefaultStream;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.system.processing.ProcessingStatusRecorder;

import de.huxhorn.sulky.ulid.ULID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.Strings.isNullOrEmpty;

public class ProcessBufferProcessor implements WorkHandler<MessageEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessBufferProcessor.class);

    private final Meter incomingMessages;

    private final Timer processTime;
    private final Meter outgoingMessages;
    private final OrderedMessageProcessors orderedMessageProcessors;

    private final OutputBuffer outputBuffer;
    private final ProcessingStatusRecorder processingStatusRecorder;
    private final ULID ulid;
    private final DecodingProcessor decodingProcessor;
    private final Provider<Stream> defaultStreamProvider;
    private volatile Message currentMessage;

    @AssistedInject
    public ProcessBufferProcessor(MetricRegistry metricRegistry,
                                  OrderedMessageProcessors orderedMessageProcessors,
                                  OutputBuffer outputBuffer,
                                  ProcessingStatusRecorder processingStatusRecorder,
                                  ULID ulid,
                                  @Assisted DecodingProcessor decodingProcessor,
                                  @DefaultStream Provider<Stream> defaultStreamProvider) {
        this.orderedMessageProcessors = orderedMessageProcessors;
        this.outputBuffer = outputBuffer;
        this.processingStatusRecorder = processingStatusRecorder;
        this.ulid = ulid;
        this.decodingProcessor = decodingProcessor;
        this.defaultStreamProvider = defaultStreamProvider;

        incomingMessages = metricRegistry.meter(name(ProcessBufferProcessor.class, "incomingMessages"));
        outgoingMessages = metricRegistry.meter(name(ProcessBufferProcessor.class, "outgoingMessages"));
        processTime = metricRegistry.timer(name(ProcessBufferProcessor.class, "processTime"));
        currentMessage = null;
    }

    @Override
    public void onEvent(MessageEvent event) throws Exception {
        try {
            // Decode the RawMessage to a Message object. The DecodingProcessor used to be a separate handler in the
            // ProcessBuffer. Due to performance problems discovered during 1.0.0 testing, we decided to move this here.
            // TODO The DecodingProcessor does not need to be a EventHandler. We decided to do it like this to keep the change as small as possible for 1.0.0.
            decodingProcessor.onEvent(event, 0L, false);

            if (event.isSingleMessage()) {
                dispatchMessage(event.getMessage());
            } else {
                final Collection<Message> messageList = event.getMessages();
                if (messageList == null) {
                    // skip message events which could not be decoded properly
                    return;
                }

                for (final Message message : messageList) {
                    dispatchMessage(message);
                }
            }
        } finally {
            event.clearMessages();
        }
    }

    public Optional<Message> getCurrentMessage() {
        return Optional.ofNullable(currentMessage);
    }

    private void dispatchMessage(final Message msg) {
        currentMessage = msg;
        incomingMessages.mark();

        LOG.debug("Starting to process message <{}>.", msg.getId());

        try (final Timer.Context ignored = processTime.time()) {
            handleMessage(msg);
            LOG.debug("Finished processing message <{}>. Writing to output buffer.", msg.getId());
        } catch (Exception e) {
            LOG.warn("Unable to process message <{}>: {}", msg.getId(), e);
        } finally {
            currentMessage = null;
            outgoingMessages.mark();
        }
    }

    private void handleMessage(@Nonnull Message msg) {
        msg.addStream(defaultStreamProvider.get());
        Messages messages = msg;

        for (MessageProcessor messageProcessor : orderedMessageProcessors) {
            messages = messageProcessor.process(messages);
        }
        for (Message message : messages) {
            if (!message.hasField(Message.FIELD_XFCOMPLIANCE_MESSAGE_ID) || isNullOrEmpty(message.getFieldAs(String.class, Message.FIELD_XFCOMPLIANCE_MESSAGE_ID))) {
                // Set the message ID once all message processors have finished
                // See documentation of Message.FIELD_XFCOMPLIANCE_MESSAGE_ID for details
                message.addField(Message.FIELD_XFCOMPLIANCE_MESSAGE_ID, ulid.nextULID());
            }

            // The processing time should only be set once all message processors have finished
            message.setProcessingTime(Tools.nowUTC());
            processingStatusRecorder.updatePostProcessingReceiveTime(message.getReceiveTime());

            outputBuffer.insertBlocking(message);
        }
    }

    public interface Factory {
        ProcessBufferProcessor create(DecodingProcessor decodingProcessor);
    }
}
