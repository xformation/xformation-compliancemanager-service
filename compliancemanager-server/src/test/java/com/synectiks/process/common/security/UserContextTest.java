/*
 * */
package com.synectiks.process.common.security;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.common.security.UserContext;
import com.synectiks.process.common.security.UserContextMissingException;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.security.PasswordAlgorithmFactory;
import com.synectiks.process.server.shared.security.Permissions;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.UserImpl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserContextTest {


    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
    }

    @Test
    void UserContextWithoutContext() {
        assertThatExceptionOfType(UserContextMissingException.class).isThrownBy(() -> new UserContext.Factory(userService).create());
    }

    @Test
    void runAs() {
        // Simulate what we do in the DefaultSecurityManagerProvider
        DefaultSecurityManager sm = new DefaultSecurityManager();
        SecurityUtils.setSecurityManager(sm);
        final DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        final DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator() {
            @Override
            public boolean isSessionStorageEnabled(Subject subject) {
                // save to session if we already have a session. do not create on just for saving the subject
                return subject.getSession(false) != null;
            }
        };
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        sm.setSubjectDAO(subjectDAO);

        final User user = new UserImpl(mock(PasswordAlgorithmFactory.class), mock(Permissions.class), ImmutableMap.of());
        when(userService.load(anyString())).thenReturn(user);
        when(userService.loadById(anyString())).thenReturn(user);

        final String USERID = "123456";
        UserContext.<Void>runAs(USERID, () -> {

            final UserContext userContext = new UserContext.Factory(userService).create();
            assertThat(userContext.getUserId()).isEqualTo(USERID);
            assertThat(userContext.getUser()).isEqualTo(user);

            return null;
        });
    }
}
