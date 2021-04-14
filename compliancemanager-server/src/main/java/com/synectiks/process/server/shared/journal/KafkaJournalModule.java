/*
 * */
package com.synectiks.process.server.shared.journal;

import com.google.inject.Scopes;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class KafkaJournalModule extends Graylog2Module {
    @Override
    protected void configure() {
        bind(Journal.class).to(KafkaJournal.class).in(Scopes.SINGLETON);
    }
}
