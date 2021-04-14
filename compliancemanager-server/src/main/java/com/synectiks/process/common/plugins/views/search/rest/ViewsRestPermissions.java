/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.plugin.security.Permission;
import com.synectiks.process.server.plugin.security.PluginPermissions;

import static com.synectiks.process.server.plugin.security.Permission.create;

import java.util.Collections;
import java.util.Set;

public class ViewsRestPermissions implements PluginPermissions {

    public static final String VIEW_READ = "view:read";
    public static final String VIEW_EDIT = "view:edit";
    public static final String VIEW_DELETE = "view:delete";
    public static final String DEFAULT_VIEW_SET = "default-view:set";

    private final ImmutableSet<Permission> permissions = ImmutableSet.of(
            create(VIEW_READ, "Read available views"),
            create(VIEW_EDIT, "Edit view"),
            create(VIEW_DELETE, "Delete view"),
            create(DEFAULT_VIEW_SET, "Set default view")
    );

    @Override
    public Set<Permission> permissions() {
        return permissions;
    }

    @Override
    public Set<Permission> readerBasePermissions() {
        return Collections.emptySet();
    }
}
