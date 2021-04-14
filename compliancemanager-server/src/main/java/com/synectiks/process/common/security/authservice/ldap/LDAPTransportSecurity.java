/*
 * */
package com.synectiks.process.common.security.authservice.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LDAPTransportSecurity {
    @JsonProperty("none")
    NONE,
    @JsonProperty("tls")
    TLS,
    @JsonProperty("start_tls")
    START_TLS
}
