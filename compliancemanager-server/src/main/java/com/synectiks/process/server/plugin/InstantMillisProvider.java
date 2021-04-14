/*
 * */
package com.synectiks.process.server.plugin;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstantMillisProvider implements DateTimeUtils.MillisProvider {
    private static final Logger log = LoggerFactory.getLogger(InstantMillisProvider.class);
    private DateTime currentTick;

    public InstantMillisProvider(DateTime instant) {
        setTimeTo(instant);
    }

    public void setTimeTo(DateTime instant) {
        log.debug("Setting clock to {}", instant);
        currentTick = instant;
    }

    @Override
    public long getMillis() {
        return currentTick.getMillis();
    }

    public void tick(Period period) {
        currentTick = currentTick.plus(period);
        log.debug("Ticking clock by {} to {}", period, currentTick);
    }
}
