/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20200204122000_MigrateUntypedViewsToDashboards;

import org.bson.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ViewState {
    private static final String FIELD_WIDGET_MAPPING = "widget_mapping";
    private final Document viewStateDocument;

    ViewState(Document viewStateDocument) {
        this.viewStateDocument = viewStateDocument;
    }

    List<Widget> widgets() {
        if (viewStateDocument.get("widgets") instanceof List) {
            @SuppressWarnings("unchecked") final Collection<Document> widgetList = viewStateDocument.get("widgets", Collection.class);
            return widgetList.stream()
                    .map(Widget::new)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    Set<String> searchTypeIdsForWidgetId(String widgetId) {
        final Document widgetMapping = viewStateDocument.get(FIELD_WIDGET_MAPPING, Document.class);
        if (widgetMapping == null) {
            return Collections.emptySet();
        }
        @SuppressWarnings("unchecked") final Collection<String> rawWidgetsList = widgetMapping.get(widgetId, Collection.class);
        if (rawWidgetsList == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(rawWidgetsList);
    }
}
