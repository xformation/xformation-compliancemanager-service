/*
 * */
/*
 * Created by IntelliJ IDEA.
 * User: kroepke
 * Date: 07/10/14
 * Time: 12:39
 */
package com.synectiks.process.server.inputs.codecs;

import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.inputs.codecs.Codec;

public class CodecsModule extends Graylog2Module {
    @Override
    protected void configure() {
        final MapBinder<String, Codec.Factory<? extends Codec>> mapBinder = codecMapBinder();

        // Aggregators must be singletons because codecs are instantiated in DecodingProcessor per message!
        bind(GelfChunkAggregator.class).in(Scopes.SINGLETON);

        installCodec(mapBinder, RawCodec.class);
        installCodec(mapBinder, SyslogCodec.class);
        installCodec(mapBinder, RandomHttpMessageCodec.class);
        installCodec(mapBinder, GelfCodec.class);
        installCodec(mapBinder, JsonPathCodec.class);
    }
}
