/*
 * */
package com.synectiks.process.common.security.authservice.ldap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface LDAPConnectorConfigProvider {
    @JsonIgnore
    LDAPConnectorConfig getLDAPConnectorConfig();
}
