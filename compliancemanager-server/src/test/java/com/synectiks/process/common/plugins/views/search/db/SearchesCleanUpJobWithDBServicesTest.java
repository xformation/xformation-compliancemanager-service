/*
 * */
package com.synectiks.process.common.plugins.views.search.db;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import com.synectiks.process.common.plugins.views.search.SearchRequirements;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.search.db.SearchesCleanUpJob;
import com.synectiks.process.common.plugins.views.search.views.ViewRequirements;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.inject.TestPasswordSecretModule;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.shared.bindings.ObjectMapperModule;
import com.synectiks.process.server.shared.bindings.ValidatorModule;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JukitoRunner.class)
@UseModules({ObjectMapperModule.class, ValidatorModule.class, TestPasswordSecretModule.class})
public class SearchesCleanUpJobWithDBServicesTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private SearchesCleanUpJob searchesCleanUpJob;
    private SearchDbService searchDbService;

    static class TestViewService extends ViewService {
        TestViewService(MongoConnection mongoConnection,
                        MongoJackObjectMapperProvider mapper,
                        ClusterConfigService clusterConfigService) {
            super(mongoConnection, mapper, clusterConfigService, view -> new ViewRequirements(Collections.emptySet(), view), mock(EntityOwnershipService.class));
        }
    }

    @Before
    public void setup(MongoJackObjectMapperProvider mapperProvider) {
        DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2018-07-03T13:37:42.000Z").getMillis());

        final ClusterConfigService clusterConfigService = mock(ClusterConfigService.class);
        final ViewService viewService = new TestViewService(
                mongodb.mongoConnection(),
                mapperProvider,
                clusterConfigService
        );
        this.searchDbService = spy(
                new SearchDbService(
                        mongodb.mongoConnection(),
                        mapperProvider,
                        dto -> new SearchRequirements(Collections.emptySet(), dto)
                )
        );
        this.searchesCleanUpJob = new SearchesCleanUpJob(viewService, searchDbService, Duration.standardDays(4));
    }

    @After
    public void tearDown() throws Exception {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testForAllEmpty() {
        this.searchesCleanUpJob.doRun();

        verify(searchDbService, never()).delete(any());
    }

    @Test
    @MongoDBFixtures("mixedExpiredAndNonExpiredSearches.json")
    public void testMixedExpiredAndNonExpiredSearches() {
        this.searchesCleanUpJob.doRun();

        final ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(searchDbService, times(1)).delete(idCaptor.capture());

        assertThat(idCaptor.getAllValues()).containsExactly("5b3b44ca77196aa4679e4da0");
    }

    @Test
    @MongoDBFixtures("mixedExpiredNonExpiredReferencedAndNonReferencedSearches.json")
    public void testMixedExpiredNonExpiredReferencedAndNonReferencedSearches() {
        this.searchesCleanUpJob.doRun();

        final ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(searchDbService, times(2)).delete(idCaptor.capture());

        assertThat(idCaptor.getAllValues()).containsExactly("5b3b44ca77196aa4679e4da1", "5b3b44ca77196aa4679e4da2");
    }

}
