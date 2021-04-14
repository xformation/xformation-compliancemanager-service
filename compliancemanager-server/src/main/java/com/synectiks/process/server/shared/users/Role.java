/*
 * */
package com.synectiks.process.server.shared.users;

import java.util.Set;

public interface Role {
    String getId();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Set<String> getPermissions();

    void setPermissions(Set<String> permissions);

    boolean isReadOnly();

}
