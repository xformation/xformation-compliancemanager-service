/*
 * */
package com.synectiks.process.server.users;

import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.shared.users.Role;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

public interface RoleService {
    Role loadById(String roleId) throws NotFoundException;

    Role load(String roleName) throws NotFoundException;

    boolean exists(String roleName);

    Set<Role> loadAll();

    Map<String, Role> loadAllIdMap() throws NotFoundException;

    Map<String, Role> findIdMap(Set<String> roleIds) throws NotFoundException;

    Map<String, Role> loadAllLowercaseNameMap() throws NotFoundException;

    Role save(Role role) throws ValidationException;

    Set<ConstraintViolation<Role>> validate(Role role);

    /**
     * Deletes the (case insensitively) named role, unless it is read only.
     * @param roleName role name to delete, case insensitive
     * @return the number of deleted roles
     */
    int delete(String roleName);

    String getAdminRoleObjectId();

    String getReaderRoleObjectId();
}
