/*
 * */
package com.synectiks.process.common.plugins.views;

import java.util.Map;

import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;

public interface Requirement<O> {
    Map<String, PluginMetadataSummary> test(O dto);
}
