/*
 * */
package com.synectiks.process.server.streams;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.plugin.streams.StreamRule;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;

public class StreamListFingerprint {
    private final String fingerprint;

    public StreamListFingerprint(List<Stream> streams) {
        this.fingerprint = buildFingerprint(streams);
    }

    public String getFingerprint() {
        return fingerprint;
    }

    private String buildFingerprint(List<Stream> streams) {
        final MessageDigest sha1Digest = DigestUtils.getSha1Digest();

        final StringBuilder sb = new StringBuilder();
        for (Stream stream : Ordering.from(getStreamComparator()).sortedCopy(streams)) {
            sb.append(stream.hashCode());

            for (StreamRule rule : Ordering.from(getStreamRuleComparator()).sortedCopy(stream.getStreamRules())) {
                sb.append(rule.hashCode());
            }
            for (Output output : Ordering.from(getOutputComparator()).sortedCopy(stream.getOutputs())) {
                sb.append(output.hashCode());
            }
        }
        return String.valueOf(Hex.encodeHex(sha1Digest.digest(sb.toString().getBytes(StandardCharsets.US_ASCII))));
    }

    private Comparator<Output> getOutputComparator() {
        return new Comparator<Output>() {
            @Override
            public int compare(Output output1, Output stream2) {
                return comparisonResult(output1.getId(), stream2.getId());
            }
        };
    }

    private Comparator<Stream> getStreamComparator() {
        return new Comparator<Stream>() {
            @Override
            public int compare(Stream stream1, Stream stream2) {
                return comparisonResult(stream1.getId(), stream2.getId());
            }
        };
    }

    private Comparator<StreamRule> getStreamRuleComparator() {
        return new Comparator<StreamRule>() {
                @Override
                public int compare(StreamRule rule1, StreamRule rule2) {
                    return comparisonResult(rule1.getId(), rule2.getId());
                }
            };
    }

    private int comparisonResult(String id1, String id2) {
        return ComparisonChain.start()
                .compare(id1, id2, String.CASE_INSENSITIVE_ORDER)
                .compare(id1, id2)
                .result();
    }
}
