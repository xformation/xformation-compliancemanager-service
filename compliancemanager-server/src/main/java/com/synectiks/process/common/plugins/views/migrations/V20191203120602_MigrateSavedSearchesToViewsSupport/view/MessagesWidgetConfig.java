/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonAutoDetect
public abstract class MessagesWidgetConfig {
    @JsonProperty("fields")
    public abstract List<String> fields();

    @JsonProperty("show_message_row")
    public abstract boolean showMessageRow();

    public static MessagesWidgetConfig create(List<String> fields, boolean showMessageRow) {
        return new AutoValue_MessagesWidgetConfig(fields, showMessageRow);
    }
}
