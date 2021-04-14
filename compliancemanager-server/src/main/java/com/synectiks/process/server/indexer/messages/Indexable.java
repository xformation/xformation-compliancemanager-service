/*
 * */
package com.synectiks.process.server.indexer.messages;

import com.codahale.metrics.Meter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import java.util.Map;

public interface Indexable {
    String getId();
    long getSize();
    DateTime getReceiveTime();
    Map<String, Object> toElasticSearchObject(ObjectMapper objectMapper,@Nonnull final Meter invalidTimestampMeter);
    DateTime getTimestamp();
}
