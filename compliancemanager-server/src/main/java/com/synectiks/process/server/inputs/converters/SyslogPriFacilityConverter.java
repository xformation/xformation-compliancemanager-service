/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.google.common.primitives.Ints;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.Map;

public class SyslogPriFacilityConverter extends Converter {
    public SyslogPriFacilityConverter(Map<String, Object> config) {
        super(Type.SYSLOG_PRI_FACILITY, config);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        final Integer priority = Ints.tryParse(value);
        if (priority == null) {
            return value;
        }

        return Tools.syslogFacilityToReadable(SyslogPriUtilities.facilityFromPriority(priority));
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }
}
