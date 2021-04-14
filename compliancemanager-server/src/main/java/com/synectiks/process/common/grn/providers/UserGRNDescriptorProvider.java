/*
 * */
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;
import com.synectiks.process.server.shared.users.UserService;

import javax.inject.Inject;
import java.util.Optional;

public class UserGRNDescriptorProvider implements GRNDescriptorProvider {
    private final UserService userService;

    @Inject
    public UserGRNDescriptorProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public GRNDescriptor get(GRN grn) {
        return Optional.ofNullable(userService.loadById(grn.entity()))
                .map(user -> GRNDescriptor.create(grn, user.getFullName()))
                .orElse(GRNDescriptor.create(grn, "ERROR: User for <" + grn.toString() + "> not found!"));
    }
}
