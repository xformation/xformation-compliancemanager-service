/*
 * */
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;

import javax.inject.Inject;
import java.util.Optional;

public class EventDefinitionGRNDescriptorProvider implements GRNDescriptorProvider {
    private final DBEventDefinitionService dbEventDefinitionService;

    @Inject
    public EventDefinitionGRNDescriptorProvider(DBEventDefinitionService dbEventDefinitionService) {
        this.dbEventDefinitionService = dbEventDefinitionService;
    }

    @Override
    public GRNDescriptor get(GRN grn) {
        final Optional<String> title = dbEventDefinitionService.get(grn.entity()).map(EventDefinitionDto::title);
        return GRNDescriptor.create(grn, title.orElse("ERROR: EventDefinition for <" + grn.toString() + "> not found!"));
    }
}
