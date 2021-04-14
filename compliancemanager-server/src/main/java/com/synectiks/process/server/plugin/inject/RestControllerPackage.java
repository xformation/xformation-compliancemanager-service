/*
 * */
package com.synectiks.process.server.plugin.inject;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RestControllerPackage {
    public abstract String name();

    public static RestControllerPackage create(String name) {
        return new AutoValue_RestControllerPackage(name);
    }
}
