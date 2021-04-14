/*
 * */
package com.synectiks.process.server.plugin.database;

import java.util.Map;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public interface EmbeddedPersistable {

    Map<String, Object> getPersistedFields();

}
