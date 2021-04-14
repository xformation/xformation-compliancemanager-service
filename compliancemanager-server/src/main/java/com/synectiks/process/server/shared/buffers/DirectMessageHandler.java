/*
 * */
package com.synectiks.process.server.shared.buffers;

import com.lmax.disruptor.WorkHandler;
import com.synectiks.process.server.plugin.journal.RawMessage;
import com.synectiks.process.server.system.processing.ProcessingStatusRecorder;

import javax.inject.Inject;

class DirectMessageHandler implements WorkHandler<RawMessageEvent> {

    private final ProcessBuffer processBuffer;
    private final ProcessingStatusRecorder processingStatusRecorder;

    @Inject
    public DirectMessageHandler(ProcessBuffer processBuffer,
                                ProcessingStatusRecorder processingStatusRecorder) {
        this.processBuffer = processBuffer;
        this.processingStatusRecorder = processingStatusRecorder;
    }

    @Override
    public void onEvent(RawMessageEvent event) throws Exception {
        final RawMessage rawMessage = event.getRawMessage();
        processBuffer.insertBlocking(rawMessage);
        if (rawMessage != null) {
            processingStatusRecorder.updateIngestReceiveTime(rawMessage.getTimestamp());
        }
        // clear out for gc and to avoid promoting the raw message event to a tenured gen
        event.setRawMessage(null);
    }


}
