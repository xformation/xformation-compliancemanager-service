/*
 * */
package com.synectiks.process.server;

import com.synectiks.process.server.shared.UI;

public class StartupException extends RuntimeException {
    private final String description;
    private final String[] docLinks;

    public StartupException(String description, String[] docLinks) {
        this.description = description;
        this.docLinks = docLinks;
    }

    @Override
    public String getMessage() {
        return UI.wallString(description, docLinks);
    }
}
