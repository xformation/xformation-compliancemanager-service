/*
 * */
package com.synectiks.process.common.security.shares;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.security.shares.EntityShareResponse.AvailableGrantee;
import com.synectiks.process.server.plugin.database.users.User;

import java.util.Set;

public interface GranteeService {
    Set<AvailableGrantee> getAvailableGrantees(User sharingUser);

    Set<User> getVisibleUsers(User requestingUser);

    Set<GRN> getGranteeAliases(GRN grantee);
}
