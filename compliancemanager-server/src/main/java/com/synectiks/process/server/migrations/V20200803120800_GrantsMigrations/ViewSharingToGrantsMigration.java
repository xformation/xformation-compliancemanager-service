/*
 * */
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.grn.GRNTypes;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.Role;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.RoleService;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewSharingToGrantsMigration {
    private static final Logger LOG = LoggerFactory.getLogger(ViewSharingToGrantsMigration.class);

    // View sharing only allowed to give other users read permissions on searches and dashboards
    private static final Capability CAPABILITY = Capability.VIEW;

    private final DBGrantService grantService;
    private final MongoCollection<Document> collection;
    private GRNRegistry grnRegistry;
    private final UserService userService;
    private final RoleService roleService;
    private final String rootUsername;
    private final ViewService viewService;

    public ViewSharingToGrantsMigration(MongoConnection mongoConnection,
                                        DBGrantService grantService,
                                        UserService userService,
                                        RoleService roleService,
                                        @Named("root_username") String rootUsername,
                                        ViewService viewService,
                                        GRNRegistry grnRegistry) {

        this.grantService = grantService;
        this.userService = userService;
        this.roleService = roleService;
        this.rootUsername = rootUsername;
        this.viewService = viewService;
        this.collection = mongoConnection.getMongoDatabase().getCollection("view_sharings", Document.class);
        this.grnRegistry = grnRegistry;
    }

    public void upgrade() {
        for (final Document document : collection.find()) {
            LOG.debug("Migrate view sharing: {}", document);

            final ObjectId sharingId = document.getObjectId("_id");
            final String sharingType = document.get("type", String.class);
            final String viewId = document.get("view_id", String.class);

            try {
                switch (sharingType) {
                    case "users":
                        //noinspection unchecked
                        migrateUsers(viewId, (Collection<String>) document.get("users", Collection.class));
                        break;
                    case "roles":
                        //noinspection unchecked
                        migrateRoles(viewId, (Collection<String>) document.get("roles", Collection.class));
                        break;
                    case "all_of_instance":
                        migrateAllOfInstance(viewId);
                        break;
                    default:
                        LOG.warn("Skipping unknown view sharing type: {}", sharingType);
                        continue; // Continue here so we don't delete the sharing document
                }

                // The view sharing document should be removed after successful migration
                deleteViewSharing(sharingId);
            } catch (Exception e) {
                LOG.error("Couldn't migrate view sharing: {}", document, e);
            }
        }
    }

    private void migrateUsers(String viewId, Collection<String> userNames) {
        final Set<User> users = userNames.stream()
                .map(userService::load)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final GRN target = getTarget(viewId);

        LOG.info("Migrate users for view <{}> to grants: {}", target, users.stream()
                .map(u -> u.getId() + "/" + u.getName())
                .collect(Collectors.toSet()));

        for (final User user : users) {
            ensureGrant(user, target);
        }
    }

    private void migrateRoles(String viewId, Collection<String> roleNames) {
        final GRN target = getTarget(viewId);

        LOG.info("Migrate roles for view <{}> to grants: {}", target, roleNames);

        final Set<Role> roles = roleNames.stream()
                .map(roleName -> {
                    try {
                        return Optional.of(roleService.load(roleName));
                    } catch (NotFoundException e) {
                        return Optional.<Role>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        for (final Role role : roles) {
            for (final User user : userService.loadAllForRole(role)) {
                ensureGrant(user, target);
            }
        }
    }

    private void migrateAllOfInstance(String viewId) {
        final GRN target = getTarget(viewId);

        LOG.info("Migrate all-of-instance for view <{}> to grants", target);

        ensureEveryoneGrant(target);
    }

    private void ensureEveryoneGrant(GRN target) {
        final GRN grantee = GRNRegistry.GLOBAL_USER_GRN;

        if (!grantService.hasGrantFor(grantee, CAPABILITY, target)) {
            grantService.create(grantee, CAPABILITY, target, rootUsername);
        }
    }

    private void ensureGrant(User user, GRN target) {
        final GRN grantee = grnRegistry.ofUser(user);

        grantService.ensure(grantee, CAPABILITY, target, rootUsername);
    }

    private GRN getTarget(String viewId) {
        final ViewDTO view = viewService.get(viewId)
                .orElseThrow(() -> new IllegalArgumentException("View <" + viewId + "> doesn't exist"));
        final GRNType grnType = ViewDTO.Type.DASHBOARD.equals(view.type()) ? GRNTypes.DASHBOARD : GRNTypes.SEARCH;

        return grnType.toGRN(viewId);
    }

    private void deleteViewSharing(ObjectId id) {
        LOG.debug("Removing obsolete view sharing document {}", id);
        collection.deleteOne(Filters.eq("_id", id));
    }
}
