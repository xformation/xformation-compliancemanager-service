/*
 * */
package com.synectiks.process.server.plugin.inputs.codecs;

import com.synectiks.process.server.plugin.AbstractDescriptor;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.journal.RawMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Codec {
    @Nullable
    Message decode(@Nonnull RawMessage rawMessage);

    @Nullable
    CodecAggregator getAggregator();

    String getName();

    @Nonnull
    Configuration getConfiguration();

    interface Factory<C> {
        C create(Configuration configuration);
        Config getConfig();
        Descriptor getDescriptor();
    }

    interface Config {
        String CK_OVERRIDE_SOURCE = "override_source";

        ConfigurationRequest getRequestedConfiguration();
        void overrideDefaultValues(@Nonnull ConfigurationRequest cr);
    }

    class Descriptor extends AbstractDescriptor {
        public Descriptor() {
            // We ensure old Codec plugins remain compatible by setting an empty name in here
            this("");
        }

        protected Descriptor(String name) {
            super(name, false, "");
        }
    }
}
