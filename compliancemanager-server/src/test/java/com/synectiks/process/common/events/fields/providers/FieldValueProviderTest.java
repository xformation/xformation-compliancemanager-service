/*
 * */
package com.synectiks.process.common.events.fields.providers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.synectiks.process.server.plugin.Message;

import java.util.Map;

public abstract class FieldValueProviderTest {
    protected Message newMessage(Map<String, Object> fields) {
        final Message message = new Message("test message", "test", DateTime.now(DateTimeZone.UTC));
        message.addFields(fields);
        return message;
    }
}
