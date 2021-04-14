/*
 * */
package com.synectiks.process.server.inputs.converters;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.Map;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class NumericConverter extends Converter {

    public NumericConverter(Map<String, Object> config) {
        super(Type.NUMERIC, config);
    }

	/**
	 * Attempts to convert the provided string value to a numeric type,
	 * trying Integer, Long and Double in order until successful.
	 */
    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        Object result = Ints.tryParse(value);

        if (result != null) {
            return result;
        }

        result = Longs.tryParse(value);

        if (result != null) {
            return result;
        }

        result = Doubles.tryParse(value);

        if (result != null) {
            return result;
        }

        return value;
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }

}
