/*
 * */
package com.synectiks.process.server.migrations.V20180214093600_AdjustDashboardPositionToNewResolution;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.migrations.V20180214093600_AdjustDashboardPositionToNewResolution.MigrationDashboard;
import com.synectiks.process.server.migrations.V20180214093600_AdjustDashboardPositionToNewResolution.MigrationDashboardService;
import com.synectiks.process.server.shared.SuppressForbidden;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MigrationDashboardServiceTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private MigrationDashboardService dashboardService;

    @Before
    @SuppressForbidden("Using Executors.newSingleThreadExecutor() is okay in tests")
    public void setUpService() {
        dashboardService = new MigrationDashboardService(mongodb.mongoConnection());
    }

    @Test
    @MongoDBFixtures("singleDashboard.json")
    public void testAll() {
        final List<MigrationDashboard> dashboards = dashboardService.all();
        final MigrationDashboard dashboard = dashboards.get(0);

        assertEquals("Should have returned exactly 1 document", 1, dashboards.size());
        assertEquals("Example dashboard", dashboard.getTitle());
    }

    @Test
    @MongoDBFixtures("singleDashboard.json")
    public void testCountSingleDashboard() throws Exception {
        assertEquals(1, this.dashboardService.count());
    }

    @Test
    public void testCountEmptyCollection() throws Exception {
        assertEquals(0, this.dashboardService.count());
    }
}
