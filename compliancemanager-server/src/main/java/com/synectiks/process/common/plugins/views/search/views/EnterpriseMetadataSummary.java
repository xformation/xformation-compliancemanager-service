/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.github.zafarkhaja.semver.Version;

import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class EnterpriseMetadataSummary extends PluginMetadataSummary {
    @Override
    public String uniqueId() {
        return "com.synectiks.process.common.plugins.enterprise.EnterprisePlugin";
    }

    @Override
    public String name() {
        return "alertmanager Enterprise";
    }

    @Override
    public String author() {
        return "alertmanager, Inc.";
    }

    @Override
    public URI url() {
        return URI.create("https://www.alertmanager.org/enterprise");
    }

    @Override
    public Version version() {
        return Version.valueOf("3.1.0");
    }

    @Override
    public String description() {
        return "alertmanager Enterprise";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EnterpriseMetadataSummary;
    }
}
