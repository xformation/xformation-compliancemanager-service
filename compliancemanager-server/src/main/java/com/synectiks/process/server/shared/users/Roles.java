/*
 * */
package com.synectiks.process.server.shared.users;

import com.google.common.base.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

public abstract class Roles {

    @Nonnull
    public static RoleIdToNameFunction roleIdToNameFunction(Map<String, Role> idMap) {
        return new RoleIdToNameFunction(idMap);
    }

    @Nonnull
    public static RoleNameToIdFunction roleNameToIdFunction(Map<String, Role> nameMap) {
        return new RoleNameToIdFunction(nameMap);
    }

    public static RoleToNameFunction roleToNameFunction() {
        return new RoleToNameFunction(false);
    }

    public static RoleToNameFunction roleToNameFunction(boolean lowerCase) {
        return new RoleToNameFunction(lowerCase);
    }

    private static class RoleIdToNameFunction implements Function<String, String> {
        private final Map<String, Role> idToRole;

        public RoleIdToNameFunction(Map<String, Role> idToRole) {
            this.idToRole = idToRole;
        }

        @Nullable
        @Override
        public String apply(String roleId) {
            if (roleId == null || !idToRole.containsKey(roleId)) {
                return null;
            }
            return idToRole.get(roleId).getName();
        }
    }

    private static class RoleNameToIdFunction implements Function<String, String> {

        private final Map<String, Role> nameToRole;

        public RoleNameToIdFunction(Map<String, Role> nameToRole) {
            this.nameToRole = nameToRole;
        }

        @Nullable
        @Override
        public String apply(@Nullable String roleName) {
            if (roleName == null) {
                return null;
            }
            final Role role = nameToRole.get(roleName.toLowerCase(Locale.ENGLISH));
            if (role == null) {
                return null;
            }
            return role.getId();
        }
    }

    private static class RoleToNameFunction implements Function<Role, String> {
        private final boolean lowerCase;

        public RoleToNameFunction(boolean lowerCase) {
            this.lowerCase = lowerCase;
        }

        @Nullable
        @Override
        public String apply(@Nullable Role input) {
            if (input != null) {
                final String name = input.getName();
                return lowerCase ? name.toLowerCase(Locale.ENGLISH) : name;
            }
            else return null;
        }
    }
}
