/*
 * */
package com.synectiks.process.server.plugin.inputs;

import java.util.Map;

public abstract class Converter {

    public enum Type {
        NUMERIC,
        DATE,
        HASH,
        SPLIT_AND_COUNT,
        SYSLOG_PRI_LEVEL,
        SYSLOG_PRI_FACILITY,
        TOKENIZER,
        IP_ANONYMIZER,
        CSV,
        LOWERCASE,
        UPPERCASE,
        FLEXDATE,
        LOOKUP_TABLE
    }

    private final Type type;
    private final Map<String, Object> config;

    public Converter(Type type, Map<String, Object> config) {
        this.type = type;
        this.config = config;
    }

    public Type getType() {
        return type;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public abstract Object convert(String value);
    public abstract boolean buildsMultipleFields();

}
