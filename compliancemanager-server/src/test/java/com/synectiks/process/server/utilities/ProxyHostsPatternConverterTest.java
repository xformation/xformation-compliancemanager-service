/*
 * */
package com.synectiks.process.server.utilities;

import org.junit.Test;

import com.synectiks.process.server.utilities.ProxyHostsPattern;
import com.synectiks.process.server.utilities.ProxyHostsPatternConverter;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyHostsPatternConverterTest {
    @Test
    public void convertFromAndTo() {
        final ProxyHostsPatternConverter converter = new ProxyHostsPatternConverter();
        final ProxyHostsPattern pattern = converter.convertFrom("127.0.0.1,node0.graylog.example.com");

        assertThat(pattern.matches("127.0.0.1")).isTrue();
        assertThat(converter.convertTo(pattern)).isEqualTo("127.0.0.1,node0.graylog.example.com");
    }
}