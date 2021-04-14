/*
 * */
package com.synectiks.process.server.shared.journal;

import com.google.inject.Scopes;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class NoopJournalModule extends Graylog2Module {
    @Override
    protected void configure() {
        serviceBinder().addBinding().to(NoopJournal.class).in(Scopes.SINGLETON);
        binder().bind(Journal.class).to(NoopJournal.class).in(Scopes.SINGLETON);

    }
}
