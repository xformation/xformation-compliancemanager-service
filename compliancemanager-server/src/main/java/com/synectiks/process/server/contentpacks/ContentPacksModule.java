/*
 * */
package com.synectiks.process.server.contentpacks;

import com.synectiks.process.server.contentpacks.constraints.GraylogVersionConstraintChecker;
import com.synectiks.process.server.contentpacks.constraints.PluginVersionConstraintChecker;
import com.synectiks.process.server.contentpacks.facades.DashboardFacade;
import com.synectiks.process.server.contentpacks.facades.GrokPatternFacade;
import com.synectiks.process.server.contentpacks.facades.InputFacade;
import com.synectiks.process.server.contentpacks.facades.LookupCacheFacade;
import com.synectiks.process.server.contentpacks.facades.LookupDataAdapterFacade;
import com.synectiks.process.server.contentpacks.facades.LookupTableFacade;
import com.synectiks.process.server.contentpacks.facades.OutputFacade;
import com.synectiks.process.server.contentpacks.facades.PipelineFacade;
import com.synectiks.process.server.contentpacks.facades.PipelineRuleFacade;
import com.synectiks.process.server.contentpacks.facades.RootEntityFacade;
import com.synectiks.process.server.contentpacks.facades.SearchFacade;
import com.synectiks.process.server.contentpacks.facades.SidecarCollectorConfigurationFacade;
import com.synectiks.process.server.contentpacks.facades.SidecarCollectorFacade;
import com.synectiks.process.server.contentpacks.facades.StreamFacade;
import com.synectiks.process.server.contentpacks.facades.UrlWhitelistFacade;
import com.synectiks.process.server.contentpacks.facades.dashboardV1.DashboardV1Facade;
import com.synectiks.process.server.contentpacks.jersey.ModelIdParamConverter;
import com.synectiks.process.server.contentpacks.model.entities.EventListEntity;
import com.synectiks.process.server.contentpacks.model.entities.MessageListEntity;
import com.synectiks.process.server.contentpacks.model.entities.PivotEntity;
import com.synectiks.process.server.plugin.PluginModule;

public class ContentPacksModule extends PluginModule {

    @Override
    protected void configure() {
        bind(ContentPackPersistenceService.class).asEagerSingleton();
        bind(ContentPackService.class).asEagerSingleton();

        jerseyAdditionalComponentsBinder().addBinding().toInstance(ModelIdParamConverter.Provider.class);

        addEntityFacade(SidecarCollectorConfigurationFacade.TYPE_V1, SidecarCollectorConfigurationFacade.class);
        addEntityFacade(SidecarCollectorFacade.TYPE_V1, SidecarCollectorFacade.class);
        addEntityFacade(GrokPatternFacade.TYPE_V1, GrokPatternFacade.class);
        addEntityFacade(InputFacade.TYPE_V1, InputFacade.class);
        addEntityFacade(LookupCacheFacade.TYPE_V1, LookupCacheFacade.class);
        addEntityFacade(LookupDataAdapterFacade.TYPE_V1, LookupDataAdapterFacade.class);
        addEntityFacade(LookupTableFacade.TYPE_V1, LookupTableFacade.class);
        addEntityFacade(OutputFacade.TYPE_V1, OutputFacade.class);
        addEntityFacade(PipelineFacade.TYPE_V1, PipelineFacade.class);
        addEntityFacade(PipelineRuleFacade.TYPE_V1, PipelineRuleFacade.class);
        addEntityFacade(RootEntityFacade.TYPE, RootEntityFacade.class);
        addEntityFacade(StreamFacade.TYPE_V1, StreamFacade.class);
        addEntityFacade(DashboardFacade.TYPE_V2, DashboardFacade.class);
        addEntityFacade(DashboardV1Facade.TYPE_V1, DashboardV1Facade.class);
        addEntityFacade(SearchFacade.TYPE_V1, SearchFacade.class);
        addEntityFacade(UrlWhitelistFacade.TYPE_V1, UrlWhitelistFacade.class);

        addConstraintChecker(GraylogVersionConstraintChecker.class);
        addConstraintChecker(PluginVersionConstraintChecker.class);

        registerJacksonSubtype(MessageListEntity.class);
        registerJacksonSubtype(PivotEntity.class);
        registerJacksonSubtype(EventListEntity.class);
    }
}
