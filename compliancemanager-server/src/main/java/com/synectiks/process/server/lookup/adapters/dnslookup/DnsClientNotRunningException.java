/*
 * */
package com.synectiks.process.server.lookup.adapters.dnslookup;

public class DnsClientNotRunningException extends RuntimeException {

    public DnsClientNotRunningException() {
        super( "The DNS client is not running." );
    }
}


