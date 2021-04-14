/*
 * */
package com.synectiks.process.server.indexer.rotation.strategies;

import com.google.common.base.MoreObjects;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;
import com.synectiks.process.server.plugin.system.NodeId;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeFieldType.hourOfDay;
import static org.joda.time.DateTimeFieldType.minuteOfHour;
import static org.joda.time.DateTimeFieldType.monthOfYear;
import static org.joda.time.DateTimeFieldType.secondOfMinute;
import static org.joda.time.DateTimeFieldType.weekOfWeekyear;
import static org.joda.time.DateTimeFieldType.year;

@Singleton
public class TimeBasedRotationStrategy extends AbstractRotationStrategy {
    private static final Logger log = LoggerFactory.getLogger(TimeBasedRotationStrategy.class);

    private final Indices indices;
    private Map<String, DateTime> lastRotation;
    private Map<String, DateTime> anchor;

    @Inject
    public TimeBasedRotationStrategy(Indices indices, NodeId nodeId,
                                     AuditEventSender auditEventSender) {
        super(auditEventSender, nodeId);
        this.anchor = new ConcurrentHashMap<>();
        this.lastRotation = new ConcurrentHashMap<>();
        this.indices = requireNonNull(indices, "indices must not be null");
    }

    @Override
    public Class<? extends RotationStrategyConfig> configurationClass() {
        return TimeBasedRotationStrategyConfig.class;
    }

    @Override
    public RotationStrategyConfig defaultConfiguration() {
        return TimeBasedRotationStrategyConfig.createDefault();
    }

    /**
     * Determines the starting point ("anchor") for a period.
     *
     * To produce repeatable rotation points in time, the period is "snapped" to a "grid" of time.
     * For example, an hourly index rotation would be anchored to the last full hour, instead of happening at whatever minute
     * the first rotation was started.
     *
     * This "snapping" is done accordingly with the other parts of a period.
     *
     * For highly irregular periods (those that do not have a small zero component)
     *
     * @param period the rotation period
     * @return the anchor DateTime to calculate rotation periods from
     */
    static DateTime determineRotationPeriodAnchor(@Nullable DateTime lastAnchor, Period period) {
        final Period normalized = period.normalizedStandard();
        int years = normalized.getYears();
        int months = normalized.getMonths();
        int weeks = normalized.getWeeks();
        int days = normalized.getDays();
        int hours = normalized.getHours();
        int minutes = normalized.getMinutes();
        int seconds = normalized.getSeconds();

        if (years == 0 && months == 0 && weeks == 0 && days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            throw new IllegalArgumentException("Invalid rotation period specified");
        }

        // find the largest non-zero stride in the period. that's our anchor type. statement order matters here!
        DateTimeFieldType largestStrideType = null;
        if (seconds > 0) largestStrideType = secondOfMinute();
        if (minutes > 0) largestStrideType = minuteOfHour();
        if (hours > 0) largestStrideType = hourOfDay();
        if (days > 0) largestStrideType = dayOfMonth();
        if (weeks > 0) largestStrideType = weekOfWeekyear();
        if (months > 0) largestStrideType = monthOfYear();
        if (years > 0) largestStrideType = year();
        if (largestStrideType == null) {
            throw new IllegalArgumentException("Could not determine rotation stride length.");
        }

        final DateTime anchorTime = anchorTimeFrom(lastAnchor);

        final DateTimeField field = largestStrideType.getField(anchorTime.getChronology());
        int periodValue = normalized.get(largestStrideType.getDurationType());
        final long fieldValue = field.roundFloor(anchorTime.getMillis());

        final int fieldValueInUnit = field.get(fieldValue);
        if (periodValue == 0) {
            log.warn("Determining stride length failed because of a 0 period. Defaulting back to 1 period to avoid crashing, but this is a bug!");
            periodValue = 1;
        }
        final long difference = fieldValueInUnit % periodValue;
        final long newValue = field.add(fieldValue, -1 * difference);
        return new DateTime(newValue, DateTimeZone.UTC);
    }

    private static DateTime anchorTimeFrom(@Nullable DateTime lastAnchor) {
        if (lastAnchor != null && !lastAnchor.getZone().equals(DateTimeZone.UTC)) {
            return lastAnchor.withZone(DateTimeZone.UTC);
        }
        return MoreObjects.firstNonNull(lastAnchor, Tools.nowUTC());
    }

    @Nullable
    @Override
    protected Result shouldRotate(String index, IndexSet indexSet) {
        final IndexSetConfig indexSetConfig = requireNonNull(indexSet.getConfig(), "Index set configuration must not be null");
        final String indexSetId = indexSetConfig.id();
        checkState(!isNullOrEmpty(index), "Index name must not be null or empty");
        checkState(!isNullOrEmpty(indexSetId), "Index set ID must not be null or empty");
        checkState(indexSetConfig.rotationStrategy() instanceof TimeBasedRotationStrategyConfig,
                "Invalid rotation strategy config <" + indexSetConfig.rotationStrategy().getClass().getCanonicalName() + "> for index set <" + indexSetId + ">");

        final TimeBasedRotationStrategyConfig config = (TimeBasedRotationStrategyConfig) indexSetConfig.rotationStrategy();
        final Period rotationPeriod = config.rotationPeriod().normalizedStandard();
        final DateTime now = Tools.nowUTC();
        // when first started, we might not know the last rotation time, look up the creation time of the index instead.
        if (!lastRotation.containsKey(indexSetId)) {
            indices.indexCreationDate(index).ifPresent(creationDate -> {
                final DateTime currentAnchor = determineRotationPeriodAnchor(creationDate, rotationPeriod);
                anchor.put(indexSetId, currentAnchor);
                lastRotation.put(indexSetId, creationDate);
            });

            // still not able to figure out the last rotation time, we'll rotate forcibly
            if (!lastRotation.containsKey(indexSetId)) {
                return new SimpleResult(true, "No known previous rotation time, forcing index rotation now.");
            }
        }

        final DateTime currentAnchor = anchor.get(indexSetId);
        final DateTime nextRotation = currentAnchor.plus(rotationPeriod);
        if (nextRotation.isAfter(now)) {
            final String message = new MessageFormat("Next rotation at {0}", Locale.ENGLISH)
                    .format(new Object[]{nextRotation});
            return new SimpleResult(false, message);
        }

        // determine new anchor (push it to within less then one period before now) in case we missed one or more periods
        DateTime tmpAnchor;
        int multiplicator = 0;
        do {
            tmpAnchor = currentAnchor.withPeriodAdded(rotationPeriod, ++multiplicator);
        } while (tmpAnchor.isBefore(now));

        final DateTime nextAnchor = currentAnchor.withPeriodAdded(rotationPeriod, multiplicator - 1);
        anchor.put(indexSetId, nextAnchor);
        lastRotation.put(indexSetId, now);
        final String message = new MessageFormat("Rotation period {0} elapsed, next rotation at {1}", Locale.ENGLISH)
                .format(new Object[]{now, nextAnchor});
        return new SimpleResult(true, message);
    }

    static class SimpleResult implements AbstractRotationStrategy.Result {
        private final String message;
        private final boolean rotate;

        SimpleResult(boolean rotate, String message) {
            this.message = message;
            this.rotate = rotate;
            log.debug("{} because of: {}", rotate ? "Rotating" : "Not rotating", message);
        }

        @Override
        public String getDescription() {
            return message;
        }

        @Override
        public boolean shouldRotate() {
            return rotate;
        }
    }
}
