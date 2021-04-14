/*
 * */
package com.synectiks.process.server.indexer;

import org.junit.jupiter.api.Test;

import com.synectiks.process.server.indexer.GIMMapping6;
import com.synectiks.process.server.indexer.IndexMappingTemplate;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;

import java.util.Map;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class GIMMapping6Test extends GIMMappingTest {
    @Test
    void matchesJsonSource() throws Exception {
        final IndexMappingTemplate template = new GIMMapping6();
        final IndexSetConfig indexSetConfig = mockIndexSetConfig();

        final Map<String, Object> result = template.toTemplate(indexSetConfig, "myindex*", -2147483648);

        assertEquals(resource("expected_gim_template6.json"), json(result), true);
    }
}
