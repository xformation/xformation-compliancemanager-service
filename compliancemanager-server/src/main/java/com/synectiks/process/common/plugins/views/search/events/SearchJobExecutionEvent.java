/*
 * */
package com.synectiks.process.common.plugins.views.search.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.server.plugin.database.users.User;

import org.joda.time.DateTime;

@AutoValue
@JsonAutoDetect
public abstract class SearchJobExecutionEvent {
    public abstract User user();
    public abstract SearchJob searchJob();
    public abstract DateTime executionStart();

    public static SearchJobExecutionEvent create(User user, SearchJob searchJob, DateTime executionStart) {
        return new AutoValue_SearchJobExecutionEvent(user, searchJob, executionStart);
    }
}
