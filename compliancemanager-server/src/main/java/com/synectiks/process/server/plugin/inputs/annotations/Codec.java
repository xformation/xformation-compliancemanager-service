/*
 * */
package com.synectiks.process.server.plugin.inputs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Codec {
    /**
     * Internal name used for identifying the codec.
     * @return
     */
    String name();

    /**
     * Human readable name.
     * @return
     */
    String displayName();
}
