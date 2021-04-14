/*
 * */
package com.synectiks.process.server.system.processing;

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.Validator;
import com.github.joschi.jadconfig.util.Duration;
import com.github.joschi.jadconfig.validators.PositiveDurationValidator;
import com.github.joschi.jadconfig.validators.PositiveIntegerValidator;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ProcessingStatusConfig {
    private static final String PREFIX = "processing_status_";
    static final String PERSIST_INTERVAL = PREFIX + "persist_interval";
    static final String UPDATE_THRESHOLD = PREFIX + "update_threshold";
    static final String JOURNAL_WRITE_RATE_THRESHOLD = PREFIX + "journal_write_rate_threshold";

    @Parameter(value = PERSIST_INTERVAL, validators = {PositiveDurationValidator.class, Minimum1SecondValidator.class})
    private Duration processingStatusPersistInterval = Duration.seconds(1);

    @Parameter(value = UPDATE_THRESHOLD, validators = {PositiveDurationValidator.class, Minimum1SecondValidator.class})
    private Duration updateThreshold = Duration.minutes(1);

    @Parameter(value = JOURNAL_WRITE_RATE_THRESHOLD, validators = PositiveIntegerValidator.class)
    private int journalWriteRateThreshold = 1;

    public Duration getProcessingStatusPersistInterval() {
        return processingStatusPersistInterval;
    }

    public Duration getUpdateThreshold() {
        return updateThreshold;
    }

    public int getJournalWriteRateThreshold() {
        return journalWriteRateThreshold;
    }

    public static class Minimum1SecondValidator implements Validator<Duration> {
        @Override
        public void validate(final String name, final Duration value) throws ValidationException {
            if (value != null && value.compareTo(Duration.seconds(1)) < 0) {
                throw new ValidationException("Parameter " + name + " should be at least 1 second (found " + value + ")");
            }
        }
    }
}
