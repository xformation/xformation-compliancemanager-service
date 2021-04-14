/*
 * */
package com.synectiks.process.server.alerts;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.UserService;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

public class EmailRecipients {
    private final UserService userService;

    private final List<String> usernames;
    private final List<String> emails;

    private Set<String> resolvedEmails;

    public interface Factory {
        EmailRecipients create(
                @Assisted("usernames") List<String> usernames,
                @Assisted("emails") List<String> emails);
    }

    @Inject
    public EmailRecipients(UserService userService,
                           @Assisted("usernames") List<String> usernames,
                           @Assisted("emails") List<String> emails) {
        this.userService = userService;
        this.usernames = usernames;
        this.emails = emails;
    }

    public Set<String> getEmailRecipients() {
        if (resolvedEmails != null) {
            return resolvedEmails;
        }

        final ImmutableSet.Builder<String> emails = ImmutableSet.builder();
        emails.addAll(this.emails);

        for (String username : usernames) {
            final User user = userService.load(username);

            if (user != null && !isNullOrEmpty(user.getEmail())) {
                // LDAP users might have multiple email addresses defined.
                final Iterable<String> addresses = Splitter.on(",").omitEmptyStrings().trimResults().split(user.getEmail());
                emails.addAll(addresses);
            }
        }

        resolvedEmails = emails.build();

        return resolvedEmails;
    }

    public boolean isEmpty() {
        return usernames.isEmpty() && emails.isEmpty();
    }
}
