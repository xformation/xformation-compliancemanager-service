/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.system.urlwhitelist.LiteralWhitelistEntry;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelist;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;
import com.synectiks.process.server.system.urlwhitelist.WhitelistEntry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlWhitelistServiceTest {
    @InjectMocks
    UrlWhitelistService urlWhitelistService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addEntry() {
        final WhitelistEntry existingEntry = LiteralWhitelistEntry.create("a", "a", "a");
        final UrlWhitelist existingWhitelist = UrlWhitelist.createEnabled(Collections.singletonList(existingEntry));
        final WhitelistEntry newEntry = LiteralWhitelistEntry.create("b", "b", "b");

        final UrlWhitelist whitelistWithEntryAdded = urlWhitelistService.addEntry(existingWhitelist, newEntry);
        assertThat(whitelistWithEntryAdded).isEqualTo(
                UrlWhitelist.createEnabled(ImmutableList.of(existingEntry, newEntry)));

        final WhitelistEntry replacedEntry = LiteralWhitelistEntry.create("a", "c", "c");

        final UrlWhitelist whitelistWithEntryReplaced =
                urlWhitelistService.addEntry(whitelistWithEntryAdded, replacedEntry);
        assertThat(whitelistWithEntryReplaced).isEqualTo(
                UrlWhitelist.createEnabled(ImmutableList.of(replacedEntry, newEntry)));

    }

    @Test
    public void removeEntry() {
        final WhitelistEntry entry = LiteralWhitelistEntry.create("a", "a", "a");
        final UrlWhitelist whitelist = UrlWhitelist.createEnabled(Collections.singletonList(entry));
        assertThat(urlWhitelistService.removeEntry(whitelist, null)).isEqualTo(whitelist);
        assertThat(urlWhitelistService.removeEntry(whitelist, "b")).isEqualTo(whitelist);
        assertThat(urlWhitelistService.removeEntry(whitelist, "a")).isEqualTo(
                UrlWhitelist.createEnabled(Collections.emptyList()));
    }
}
