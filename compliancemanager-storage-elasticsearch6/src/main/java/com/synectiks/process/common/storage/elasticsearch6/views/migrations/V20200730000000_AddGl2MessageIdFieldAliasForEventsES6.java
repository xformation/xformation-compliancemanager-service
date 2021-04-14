/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.migrations;

import java.util.Set;

import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;

public class V20200730000000_AddGl2MessageIdFieldAliasForEventsES6 implements V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter {
    @Override
    public void addXfAlertMessageIdFieldAlias(Set<String> indexPrefixes) {
        throw new IllegalStateException("Field aliases are not supported for all minor versions of ES6. This should never be called.");
    }
}
