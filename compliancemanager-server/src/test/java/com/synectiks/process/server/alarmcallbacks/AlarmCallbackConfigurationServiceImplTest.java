/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationServiceImpl;
import com.synectiks.process.server.database.MongoDBServiceTest;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.alarmcallbacks.requests.CreateAlarmCallbackRequest;
import com.synectiks.process.server.streams.StreamImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmCallbackConfigurationServiceImplTest extends MongoDBServiceTest {
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;

    @Before
    public void setUpService() throws Exception {
        this.alarmCallbackConfigurationService = new AlarmCallbackConfigurationServiceImpl(mongodb.mongoConnection(), mapperProvider);
    }

    @Test
    @MongoDBFixtures("alarmCallbackConfigurationsSingleDocument.json")
    public void testGetForStreamIdSingleDocument() throws Exception {
        final List<AlarmCallbackConfiguration> configs = alarmCallbackConfigurationService.getForStreamId("5400deadbeefdeadbeefaffe");

        assertNotNull("Returned list should not be null", configs);
        assertEquals("Returned list should contain a single document", 1, configs.size());
    }

    @Test
    @MongoDBFixtures("alarmCallbackConfigurationsSingleDocumentStringDate.json")
    public void testGetForStreamIdSingleDocumentStringDate() throws Exception {
        testGetForStreamIdSingleDocument();
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocument.json", "alarmCallbackConfigurationsSingleDocument2.json"})
    public void testGetForStreamIdMultipleDocuments() throws Exception {
        assertEquals("There should be multiple documents in the collection", 2, alarmCallbackConfigurationService.count());
        testGetForStreamIdSingleDocument();
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocumentStringDate.json", "alarmCallbackConfigurationsSingleDocumentStringDate2.json"})
    public void testGetForStreamIdMultipleDocumentsStringDate() throws Exception {
        testGetForStreamIdMultipleDocuments();
    }

    @Test
    @MongoDBFixtures("alarmCallbackConfigurationsSingleDocument.json")
    public void testGetForStreamSingleDocument() throws Exception {
        final Stream stream = mock(StreamImpl.class);
        final String streamId = "5400deadbeefdeadbeefaffe";
        when(stream.getId()).thenReturn(streamId);

        final List<AlarmCallbackConfiguration> configs = alarmCallbackConfigurationService.getForStream(stream);
        final AlarmCallbackConfiguration alarmCallback = configs.get(0);

        assertNotNull("Returned list should not be null", configs);
        assertEquals("Returned list should contain a single document", 1, configs.size());
        assertNotNull("Returned Alarm Callback should not be null", alarmCallback);
    }

    @Test
    @MongoDBFixtures("alarmCallbackConfigurationsSingleDocumentStringDate.json")
    public void testGetForStreamSingleDocumentStringDate() throws Exception {
        testGetForStreamSingleDocument();
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocument.json", "alarmCallbackConfigurationsSingleDocument2.json"})
    public void testGetForStreamMultipleDocuments() throws Exception {
        assertEquals("There should be multiple documents in the collection", 2, alarmCallbackConfigurationService.count());
        testGetForStreamSingleDocument();
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocumentStringDate.json", "alarmCallbackConfigurationsSingleDocumentStringDate2.json"})
    public void testGetForStreamMultipleDocumentsStringDate() throws Exception {
        testGetForStreamMultipleDocuments();
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocumentStringDate.json", "alarmCallbackConfigurationsSingleDocument2.json"})
    public void testGetForStreamMultipleDocumentsMixedDates() throws Exception {
        testGetForStreamMultipleDocuments();
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocument.json", "alarmCallbackConfigurationsSingleDocument2.json"})
    public void testLoadExistingDocument() throws Exception {
        final AlarmCallbackConfiguration config = alarmCallbackConfigurationService.load("54e3deadbeefdeadbeefaffe");

        assertNotNull("Returned AlarmCallback configuration should not be null", config);
        assertEquals("AlarmCallbackConfiguration should be of dummy type", "dummy.type", config.getType());
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocumentStringDate.json", "alarmCallbackConfigurationsSingleDocument2.json"})
    public void testLoadExistingDocumentStringDate() throws Exception {
        final AlarmCallbackConfiguration config = alarmCallbackConfigurationService.load("54e3deadbeefdeadbeefaffe");

        assertNotNull("Returned AlarmCallback configuration should not be null", config);
        assertEquals("AlarmCallbackConfiguration should be of dummy type", "dummy.type", config.getType());
    }

    @Test
    public void testLoadNonExistingDocument() throws Exception {
        final AlarmCallbackConfiguration config = alarmCallbackConfigurationService.load(new ObjectId().toHexString());

        assertNull("No AlarmCallbackConfiguration should have been returned", config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadInvalidObjectId() throws Exception {
        final AlarmCallbackConfiguration config = alarmCallbackConfigurationService.load("foobar");
    }

    @Test
    public void testCreate() throws Exception {
        final CreateAlarmCallbackRequest request = CreateAlarmCallbackRequest.create("", "", Collections.emptyMap());

        final String streamId = "54e3deadbeefdeadbeefaffe";
        final String userId = "someuser";

        final AlarmCallbackConfiguration alarmCallbackConfiguration = this.alarmCallbackConfigurationService.create(streamId, request, userId);

        assertNotNull(alarmCallbackConfiguration);
        assertEquals(streamId, alarmCallbackConfiguration.getStreamId());
        assertEquals("Create should not save the object", 0, alarmCallbackConfigurationService.count());
    }

    @Test
    @MongoDBFixtures({"alarmCallbackConfigurationsSingleDocument.json", "alarmCallbackConfigurationsSingleDocument2.json"})
    public void testDeleteAlarmCallback() throws Exception {
        assertEquals("There should be multiple documents in the collection", 2, alarmCallbackConfigurationService.count());
        final AlarmCallbackConfiguration alarmCallback = mock(AlarmCallbackConfiguration.class);
        when (alarmCallback.getId()).thenReturn("54e3deadbeefdeadbeefaffe");

        alarmCallbackConfigurationService.destroy(alarmCallback);

        assertEquals("After deletion, there should be only one document left in the collection", 1, alarmCallbackConfigurationService.count());
    }
}
