/*
 * */
package com.synectiks.process.server.plugin;

import static com.codahale.metrics.MetricRegistry.name;

public final class GlobalMetricNames {

    private GlobalMetricNames() {}

    public static final String OLDEST_SEGMENT_SUFFIX = "oldest-segment";
    public static final String RATE_SUFFIX = "1-sec-rate";

    public static final String INPUT_THROUGHPUT = "com.synectiks.process.server.throughput.input";

    public static final String OUTPUT_THROUGHPUT = "com.synectiks.process.server.throughput.output";
    public static final String OUTPUT_THROUGHPUT_RATE = name(OUTPUT_THROUGHPUT, RATE_SUFFIX);

    public static final String INPUT_TRAFFIC = "com.synectiks.process.server.traffic.input";
    public static final String DECODED_TRAFFIC = "com.synectiks.process.server.traffic.decoded";
    public static final String OUTPUT_TRAFFIC = "com.synectiks.process.server.traffic.output";
    public static final String SYSTEM_OUTPUT_TRAFFIC = "com.synectiks.process.server.traffic.system-output-traffic";

    public static final String INPUT_BUFFER_USAGE = "com.synectiks.process.server.buffers.input.usage";
    public static final String INPUT_BUFFER_SIZE = "com.synectiks.process.server.buffers.input.size";

    public static final String PROCESS_BUFFER_USAGE = "com.synectiks.process.server.buffers.process.usage";
    public static final String PROCESS_BUFFER_SIZE = "com.synectiks.process.server.buffers.process.size";

    public static final String OUTPUT_BUFFER_USAGE = "com.synectiks.process.server.buffers.output.usage";
    public static final String OUTPUT_BUFFER_SIZE = "com.synectiks.process.server.buffers.output.size";

    public static final String JOURNAL_APPEND_RATE = name("com.synectiks.process.server.journal.append", RATE_SUFFIX);
    public static final String JOURNAL_READ_RATE = name("com.synectiks.process.server.journal.read", RATE_SUFFIX);
    public static final String JOURNAL_SEGMENTS = "com.synectiks.process.server.journal.segments";
    public static final String JOURNAL_UNCOMMITTED_ENTRIES = "com.synectiks.process.server.journal.entries-uncommitted";
    public static final String JOURNAL_SIZE = "com.synectiks.process.server.journal.size";
    public static final String JOURNAL_SIZE_LIMIT = "com.synectiks.process.server.journal.size-limit";
    public static final String JOURNAL_UTILIZATION_RATIO = "com.synectiks.process.server.journal.utilization-ratio";
    public static final String JOURNAL_OLDEST_SEGMENT = name("com.synectiks.process.server.journal", OLDEST_SEGMENT_SUFFIX);
}
