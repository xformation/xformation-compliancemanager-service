/*
 * */
package com.synectiks.process.common.plugins.views.search.db;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.server.plugin.periodical.Periodical;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchesCleanUpJob extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(SearchesCleanUpJob.class);

    private final ViewService viewService;
    private final SearchDbService searchDbService;
    private final Instant mustNotBeOlderThan;

    @Inject
    public SearchesCleanUpJob(ViewService viewService,
                              SearchDbService searchDbService,
                              @Named("views_maximum_search_age") Duration maximumSearchAge) {
        this.viewService = viewService;
        this.searchDbService = searchDbService;
        this.mustNotBeOlderThan = Instant.now().minus(maximumSearchAge);
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return false;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 3600;
    }

    @Override
    public int getPeriodSeconds() {
        return Duration.standardHours(8).toStandardSeconds().getSeconds();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void doRun() {
        final Set<String> requiredIds = viewService.streamAll().map(ViewDTO::searchId).collect(Collectors.toSet());
        searchDbService.streamAll()
                .filter(search -> search.createdAt().isBefore(mustNotBeOlderThan) && !requiredIds.contains(search.id()))
                .forEach(search -> searchDbService.delete(search.id()));
    }
}
