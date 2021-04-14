/*
 * */
package com.synectiks.process.common.events.processor.aggregation;

import com.synectiks.process.common.events.processor.EventDefinition;
import com.synectiks.process.common.events.processor.EventProcessorException;

public interface AggregationSearch {
    interface Factory {
        AggregationSearch create(AggregationEventProcessorConfig config,
                                 AggregationEventProcessorParameters parameters,
                                 String searchOwner,
                                 EventDefinition eventDefinition);
    }

    AggregationResult doSearch() throws EventProcessorException;
}
