/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import javax.ws.rs.Path;

public class RedirectResource {
    protected static String pathForClass(Class<?> resourceClass) {
        return resourceClass.getAnnotation(Path.class).value();
    }
}
