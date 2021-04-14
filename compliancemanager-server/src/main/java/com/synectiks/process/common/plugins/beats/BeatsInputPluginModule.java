/*
 * */
package com.synectiks.process.common.plugins.beats;

import com.synectiks.process.server.plugin.PluginModule;

public class BeatsInputPluginModule extends PluginModule {
    @Override
    protected void configure() {
        addTransport("beats", BeatsTransport.class);

        // Beats deprecated input
        addCodec("beats-deprecated", BeatsCodec.class);
        addMessageInput(BeatsInput.class);

        // Beats input with improved field handling
        addCodec("beats", Beats2Codec.class);
        addMessageInput(Beats2Input.class);
    }
}
