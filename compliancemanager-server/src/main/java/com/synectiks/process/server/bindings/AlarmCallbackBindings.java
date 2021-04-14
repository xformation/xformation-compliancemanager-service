/*
 * */
package com.synectiks.process.server.bindings;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.alarmcallbacks.EmailAlarmCallback;
import com.synectiks.process.server.alarmcallbacks.HTTPAlarmCallback;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class AlarmCallbackBindings extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<AlarmCallback> alarmCallbackBinder = Multibinder.newSetBinder(binder(), AlarmCallback.class);
        alarmCallbackBinder.addBinding().to(EmailAlarmCallback.class);
        alarmCallbackBinder.addBinding().to(HTTPAlarmCallback.class);

        TypeLiteral<Class<? extends AlarmCallback>> type = new TypeLiteral<Class<? extends AlarmCallback>>(){};
        Multibinder<Class<? extends AlarmCallback>> alarmCallbackClassBinder = Multibinder.newSetBinder(binder(), type);
        alarmCallbackClassBinder.addBinding().toInstance(EmailAlarmCallback.class);
        alarmCallbackClassBinder.addBinding().toInstance(HTTPAlarmCallback.class);
    }
}
