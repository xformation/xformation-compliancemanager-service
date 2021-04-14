/*
 * */
package com.synectiks.process.common.events.legacy;

import com.google.inject.Injector;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import com.synectiks.process.server.plugin.configuration.Configuration;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

public class LegacyAlarmCallbackFactory {
    private Injector injector;
    private final Set<Class<? extends AlarmCallback>> availableAlarmCallbacks;

    @Inject
    public LegacyAlarmCallbackFactory(Injector injector,
                                      Set<Class<? extends AlarmCallback>> availableAlarmCallbacks) {
        this.injector = injector;
        this.availableAlarmCallbacks = availableAlarmCallbacks;
    }

    public AlarmCallback create(String type, Map<String, Object> configuration) throws ClassNotFoundException, AlarmCallbackConfigurationException {
        AlarmCallback alarmCallback = create(type);
        alarmCallback.initialize(new Configuration(configuration));

        return alarmCallback;
    }

    private AlarmCallback create(String type) throws ClassNotFoundException {
        for (Class<? extends AlarmCallback> availableClass : availableAlarmCallbacks) {
            if (availableClass.getCanonicalName().equals(type)) {
                return create(availableClass);
            }
        }
        throw new RuntimeException("No class found for type " + type);
    }

    private AlarmCallback create(Class<? extends AlarmCallback> alarmCallbackClass) {
        return injector.getInstance(alarmCallbackClass);
    }
}
