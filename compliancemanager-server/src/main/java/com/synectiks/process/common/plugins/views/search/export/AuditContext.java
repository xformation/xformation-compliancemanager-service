/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import java.util.Optional;

public class AuditContext {
    private final String userName;
    private final String searchId;
    private final String searchTypeId;

    public AuditContext(String userName, String searchId, String searchTypeId) {
        this.userName = userName;
        this.searchId = searchId;
        this.searchTypeId = searchTypeId;
    }

    public String userName() {
        return userName;
    }

    public Optional<String> searchId() {
        return Optional.ofNullable(searchId);
    }

    public Optional<String> searchTypeId() {
        return Optional.ofNullable(searchTypeId);
    }
}
