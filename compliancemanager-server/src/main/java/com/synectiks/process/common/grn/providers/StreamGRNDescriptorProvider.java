/*
 * */
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamService;

import javax.inject.Inject;

public class StreamGRNDescriptorProvider implements GRNDescriptorProvider {
    private final StreamService streamService;

    @Inject
    public StreamGRNDescriptorProvider(StreamService streamService) {
        this.streamService = streamService;
    }

    @Override
    public GRNDescriptor get(GRN grn) {
        try {
            final Stream stream = streamService.load(grn.entity());
            return GRNDescriptor.create(grn, stream.getTitle());
        } catch (NotFoundException e) {
            return GRNDescriptor.create(grn, "ERROR: Stream for <" + grn.toString() + "> not found!");
        }
    }
}
