/*
 * */
package com.synectiks.process.server.system.activities;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.database.Persisted;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface SystemMessage extends Persisted {
    String getCaller();

    String getContent();

    DateTime getTimestamp();

    String getNodeId();
}
