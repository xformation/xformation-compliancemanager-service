/*
 * */
package com.synectiks.process.server.alerts;

import com.synectiks.process.server.alerts.types.FieldContentValueAlertCondition;
import com.synectiks.process.server.alerts.types.FieldValueAlertCondition;
import com.synectiks.process.server.alerts.types.MessageCountAlertCondition;
import com.synectiks.process.server.plugin.PluginModule;

public class AlertConditionBindings extends PluginModule {
    @Override
    protected void configure() {
        addAlertCondition(AbstractAlertCondition.Type.FIELD_CONTENT_VALUE.toString(),
            FieldContentValueAlertCondition.class,
            FieldContentValueAlertCondition.Factory.class);
        addAlertCondition(AbstractAlertCondition.Type.FIELD_VALUE.toString(),
            FieldValueAlertCondition.class,
            FieldValueAlertCondition.Factory.class);
        addAlertCondition(AbstractAlertCondition.Type.MESSAGE_COUNT.toString(),
            MessageCountAlertCondition.class,
            MessageCountAlertCondition.Factory.class);
    }
}
