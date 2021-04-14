/*
 * */
package com.synectiks.process.server.shared.journal;

import com.google.common.util.concurrent.Service;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class JournalReaderModule extends Graylog2Module {

    @Override
    protected void configure() {
        final Multibinder<Service> serviceBinder = serviceBinder();
        serviceBinder.addBinding().to(JournalReader.class).in(Scopes.SINGLETON);
        serviceBinder.addBinding().to(KafkaJournal.class).in(Scopes.SINGLETON);

    }

}
