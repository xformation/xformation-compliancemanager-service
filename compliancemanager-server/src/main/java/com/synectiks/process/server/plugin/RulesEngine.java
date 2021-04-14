/*
 * */
package com.synectiks.process.server.plugin;

import java.io.Closeable;

public interface RulesEngine {
    interface RulesSession extends Closeable {

        int evaluate(Message message, boolean retractFacts);

        Object insertFact(Object fact);

        boolean deleteFact(Object fact);
    }

    boolean addRule(String ruleSource);

    boolean addRulesFromFile(String rulesFile);

    int evaluateInSharedSession(Message message);

    RulesSession createPrivateSession();

    Object insertFact(Object fact);

    boolean deleteFact(Object fact);
}
