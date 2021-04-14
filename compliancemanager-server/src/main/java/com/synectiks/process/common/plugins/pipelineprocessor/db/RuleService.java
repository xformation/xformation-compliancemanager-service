/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db;

import java.util.Collection;
import java.util.Optional;

import com.synectiks.process.server.database.NotFoundException;

public interface RuleService {
    RuleDao save(RuleDao rule);

    RuleDao load(String id) throws NotFoundException;

    RuleDao loadByName(String name) throws NotFoundException;

    default Optional<RuleDao> findByName(String name) {
        try {
           RuleDao ruleDao = this.loadByName(name);
           return Optional.of(ruleDao);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    Collection<RuleDao> loadAll();

    void delete(String id);

    Collection<RuleDao> loadNamed(Collection<String> ruleNames);
}
