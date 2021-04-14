/*
 * */
package com.synectiks.process.server.alerts.types;

import org.joda.time.DateTime;

import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.Map;

public class DummyAlertCondition extends AbstractAlertCondition {
    final String description = "Dummy alert to test notifications";

    public DummyAlertCondition(Stream stream, String id, DateTime createdAt, String creatorUserId, Map<String, Object> parameters, String title) {
        super(stream, id, Type.DUMMY.toString(), createdAt, creatorUserId, parameters, title);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public CheckResult runCheck() {
        return new CheckResult(true, this, this.description, Tools.nowUTC(), null);
    }
}
