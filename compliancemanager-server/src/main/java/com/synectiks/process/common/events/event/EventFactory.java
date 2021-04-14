/*
 * */
package com.synectiks.process.common.events.event;

import org.joda.time.DateTime;

import com.synectiks.process.common.events.processor.EventDefinition;

public interface EventFactory {
    Event createEvent(EventDefinition eventDefinition, DateTime eventTime, String message);
}
