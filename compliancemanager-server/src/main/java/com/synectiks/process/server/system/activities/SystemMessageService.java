/*
 * */
package com.synectiks.process.server.system.activities;

import java.util.List;
import java.util.Map;

import com.synectiks.process.server.plugin.database.PersistedService;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface SystemMessageService extends PersistedService {
    List<SystemMessage> all(int page);
    long totalCount();
    SystemMessage create(Map<String, Object> fields);
}
