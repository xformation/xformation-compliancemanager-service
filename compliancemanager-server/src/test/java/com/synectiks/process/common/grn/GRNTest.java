/*
 * */
package com.synectiks.process.common.grn;

import org.junit.Test;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class GRNTest {

    private static final GRNRegistry GRN_REGISTRY = GRNRegistry.createWithBuiltinTypes();

    @Test
    public void parse() {
        final String testGRN = "grn::::stream:000000000000000001";
        final GRN grn = GRN.parse(testGRN, GRN_REGISTRY);

        assertEquals(grn.type(), "stream");
        assertEquals(grn.entity(), "000000000000000001");

        assertEquals(grn.toString(), testGRN);
    }

    @Test
    public void parseNormalize() {
        final String testGRN = "gRN::::stREAM:000000000000000001";
        final GRN grn = GRN.parse(testGRN, GRN_REGISTRY);

        assertEquals(grn.type(), "stream");
        assertEquals(grn.entity(), "000000000000000001");

        assertEquals(grn.toString(), testGRN.toLowerCase(Locale.ENGLISH));
    }

    @Test
    public void builderWithEntitytTest() {
        final GRN grn = GRNType.create("dashboard", "dashboards:").newGRNBuilder().entity("54e3deadbeefdeadbeef0000").build();

        assertEquals(grn.toString(), "grn::::dashboard:54e3deadbeefdeadbeef0000");
    }

    @Test
    public void compareGRNs() {
        final String testGRN = "grn::::stream:000000000000000002";
        final GRN grn1 = GRN.parse(testGRN, GRN_REGISTRY);
        final GRN grn2 = GRNType.create("stream", "streams:").newGRNBuilder().entity("000000000000000002").build();

        assertEquals(grn1, grn2);
    }

}
