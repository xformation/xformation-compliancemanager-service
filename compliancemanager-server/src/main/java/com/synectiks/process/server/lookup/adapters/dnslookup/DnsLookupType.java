/*
 * */
package com.synectiks.process.server.lookup.adapters.dnslookup;

public enum DnsLookupType {

    A,
    AAAA,
    A_AAAA, // Performs both A and AAAA lookup
    PTR,
    TXT
}
