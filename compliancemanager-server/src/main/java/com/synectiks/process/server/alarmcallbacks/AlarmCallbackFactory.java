/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.google.inject.Injector;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import com.synectiks.process.server.plugin.configuration.Configuration;

import javax.inject.Inject;
import java.util.Set;

public class AlarmCallbackFactory {
    private Injector injector;
    private final Set<Class<? extends AlarmCallback>> availableAlarmCallbacks;

    @Inject
    public AlarmCallbackFactory(Injector injector,
                                Set<Class<? extends AlarmCallback>> availableAlarmCallbacks) {
        this.injector = injector;
        this.availableAlarmCallbacks = availableAlarmCallbacks;
    }

    public AlarmCallback create(AlarmCallbackConfiguration configuration) throws ClassNotFoundException, AlarmCallbackConfigurationException {
        AlarmCallback alarmCallback = create(configuration.getType());
        alarmCallback.initialize(new Configuration(configuration.getConfiguration()));

        return alarmCallback;
    }

    public AlarmCallback create(String type) throws ClassNotFoundException {
        for (Class<? extends AlarmCallback> availableClass : availableAlarmCallbacks) {
            if (availableClass.getCanonicalName().equals(type))
                return create(availableClass);
        }
        throw new RuntimeException("No class found for type " + type);
    }

    public AlarmCallback create(Class<? extends AlarmCallback> alarmCallbackClass) {
        return injector.getInstance(alarmCallbackClass);
    }
}
