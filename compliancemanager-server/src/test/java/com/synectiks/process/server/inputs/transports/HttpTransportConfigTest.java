/*
 * */
package com.synectiks.process.server.inputs.transports;

import org.junit.Test;

import com.synectiks.process.server.inputs.transports.HttpTransport;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpTransportConfigTest {
    @Test
    public void testGetRequestedConfiguration() {
        HttpTransport.Config config = new HttpTransport.Config();

        final ConfigurationRequest requestedConfiguration = config.getRequestedConfiguration();
        assertTrue(requestedConfiguration.containsField(HttpTransport.CK_ENABLE_CORS));
        assertEquals(ConfigurationField.Optional.OPTIONAL, requestedConfiguration.getField(HttpTransport.CK_ENABLE_CORS).isOptional());
        assertEquals(true, requestedConfiguration.getField(HttpTransport.CK_ENABLE_CORS).getDefaultValue());

        assertTrue(requestedConfiguration.containsField(HttpTransport.CK_MAX_CHUNK_SIZE));
        assertEquals(ConfigurationField.Optional.OPTIONAL, requestedConfiguration.getField(HttpTransport.CK_MAX_CHUNK_SIZE).isOptional());
        assertEquals(65536, requestedConfiguration.getField(HttpTransport.CK_MAX_CHUNK_SIZE).getDefaultValue());
    }
}
