/*
 * */
package com.synectiks.process.server.inputs;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.database.Persisted;

import java.util.Map;

public interface Input extends Persisted {
    String getTitle();

    DateTime getCreatedAt();

    Map<String, Object> getConfiguration();

    Map<String, String> getStaticFields();

    String getType();

    String getCreatorUserId();

    Boolean isGlobal();

    String getContentPack();

    String getNodeId();
}
