/*
 * */
package com.synectiks.process.common.plugins.views;

import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.common.plugins.views.search.QueryMetadataDecorator;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.engine.GeneratedQueryContext;
import com.synectiks.process.common.plugins.views.search.engine.QueryBackend;
import com.synectiks.process.common.plugins.views.search.engine.QueryStringDecorator;
import com.synectiks.process.common.plugins.views.search.export.ExportBackend;
import com.synectiks.process.common.plugins.views.search.rest.SeriesDescription;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSpec;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.plugin.VersionAwareModule;

public abstract class ViewsModule extends VersionAwareModule {
    protected LinkedBindingBuilder<ExportBackend> bindExportBackend(Version supportedVersion) {
        return bindForVersion(supportedVersion, ExportBackend.class);
    }

    protected void registerQueryMetadataDecorator(Class<? extends QueryMetadataDecorator> queryMetadataDecorator) {
        queryMetadataDecoratorBinder().addBinding().to(queryMetadataDecorator);
    }

    protected Multibinder<QueryMetadataDecorator> queryMetadataDecoratorBinder() {
        return Multibinder.newSetBinder(binder(), QueryMetadataDecorator.class);
    }

    protected void registerProvidedViewsCapability(String capability, PluginMetaData plugin) {
        viewsCapabilityBinder().addBinding(capability).toInstance(plugin);
    }

    protected MapBinder<String, PluginMetaData> viewsCapabilityBinder() {
        return MapBinder.newMapBinder(binder(), String.class, PluginMetaData.class);
    }

    protected void registerViewRequirement(Class<? extends Requirement<ViewDTO>> viewRequirement) {
        viewRequirementBinder().addBinding().to(viewRequirement);
    }

    protected Multibinder<Requirement<ViewDTO>> viewRequirementBinder() {
        return Multibinder.newSetBinder(binder(), new TypeLiteral<Requirement<ViewDTO>>() {});
    }

    protected void registerSearchRequirement(Class<? extends Requirement<Search>> searchRequirement) {
        searchRequirementBinder().addBinding().to(searchRequirement);
    }

    protected Multibinder<Requirement<Search>> searchRequirementBinder() {
        return Multibinder.newSetBinder(binder(), new TypeLiteral<Requirement<Search>>() {});
    }

    protected MapBinder<String, SeriesDescription> seriesSpecBinder() {
        return MapBinder.newMapBinder(binder(), String.class, SeriesDescription.class);
    }

    protected void registerPivotAggregationFunction(String name, Class<? extends SeriesSpec> seriesSpecClass) {
        registerJacksonSubtype(seriesSpecClass);
        seriesSpecBinder().addBinding(name).toInstance(SeriesDescription.create(name));
    }

    protected MapBinder<String, QueryBackend<? extends GeneratedQueryContext>> queryBackendBinder(Version version) {
        return MapBinder.newMapBinder(binder(),
                TypeLiteral.get(String.class),
                new TypeLiteral<QueryBackend<? extends GeneratedQueryContext>>() {});

    }

    protected ScopedBindingBuilder registerQueryBackend(Version version, String name, Class<? extends QueryBackend<? extends GeneratedQueryContext>> implementation) {
        return queryBackendBinder(version).addBinding(name).to(implementation);
    }

    protected void registerESQueryDecorator(Class<? extends QueryStringDecorator> esQueryDecorator) {
        esQueryDecoratorBinder().addBinding().to(esQueryDecorator);
    }

    protected Multibinder<QueryStringDecorator> esQueryDecoratorBinder() {
        return Multibinder.newSetBinder(binder(), QueryStringDecorator.class);
    }

}
