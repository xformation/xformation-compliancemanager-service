/*
 * */
package com.synectiks.process.server.configuration;

import com.github.joschi.jadconfig.JadConfig;
import com.github.joschi.jadconfig.RepositoryException;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.repositories.InMemoryRepository;
import com.synectiks.process.server.configuration.ElasticsearchConfiguration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ElasticsearchConfigurationTest {
    @Test
    @SuppressWarnings("deprecation")
    public void testGetElasticSearchIndexPrefix() throws RepositoryException, ValidationException {
        ElasticsearchConfiguration configuration = new ElasticsearchConfiguration();
        new JadConfig(new InMemoryRepository(), configuration).process();

        assertEquals("graylog", configuration.getIndexPrefix());
    }
}
