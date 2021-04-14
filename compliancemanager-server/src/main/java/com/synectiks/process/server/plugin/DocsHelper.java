/*
 * */
package com.synectiks.process.server.plugin;

public enum DocsHelper {
    PAGE_SENDING_JSONPATH("sending_data.html#json-path-from-http-api-input"),
    PAGE_SENDING_IPFIXPATH("integrations/inputs/ipfix_input.html"),
    PAGE_ES_CONFIGURATION("configuration/elasticsearch.html"),
    PAGE_ES_VERSIONS("configuration/elasticsearch.html#elasticsearch-versions");

    private static final String DOCS_URL = "http://docs.alertmanager.org/en/";

    private final String path;

    DocsHelper(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        final com.github.zafarkhaja.semver.Version version = Version.CURRENT_CLASSPATH.getVersion();
        final String shortVersion = version.getMajorVersion() + "." + version.getMinorVersion();

        return DOCS_URL + shortVersion + "/pages/" + path;
    }

    public String toLink(String title) {
        return "<a href=\"" + toString() + "\" target=\"_blank\">" + title + "</a>";
    }
}
