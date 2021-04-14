/*
 * */
package com.synectiks.process.server.security;

import javax.net.ssl.TrustManager;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface TrustManagerProvider {
    TrustManager create(String host) throws KeyStoreException, NoSuchAlgorithmException;

    TrustManager create(List<String> host) throws KeyStoreException, NoSuchAlgorithmException;
}
