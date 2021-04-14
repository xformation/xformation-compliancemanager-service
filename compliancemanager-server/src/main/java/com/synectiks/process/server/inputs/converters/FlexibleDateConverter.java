/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FlexibleDateConverter extends AbstractDateConverter {
    public FlexibleDateConverter(Map<String, Object> config) {
        super(Type.FLEXDATE, config);
    }

    @Override
    @Nullable
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        final Parser parser = new Parser(timeZone.toTimeZone());
        final List<DateGroup> r = parser.parse(value);

        if (r.isEmpty() || r.get(0).getDates().isEmpty()) {
            return null;
        }

        return new DateTime(r.get(0).getDates().get(0), timeZone);
    }
}
