/*
 * */
package com.synectiks.process.server.inputs.extractors;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.plugin.inputs.Converter;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractExtractorTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    protected MetricRegistry metricRegistry;

    @Before
    public void setUp() throws Exception {
        metricRegistry = new MetricRegistry();
    }

    static List<Converter> noConverters() {
        return Collections.emptyList();
    }

    static Map<String, Object> noConfig() {
        return Collections.emptyMap();
    }
}
