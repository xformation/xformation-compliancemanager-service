/*
 * */
package com.synectiks.process.server.shared.security.tls;

import com.google.common.io.Resources;
import com.synectiks.process.server.shared.security.tls.PemReader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PemReaderTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void readCertificatesHandlesSingleCertificate() throws Exception {
        final URL url = Resources.getResource("org/graylog2/shared/security/tls/single.crt");
        final List<byte[]> certificates = PemReader.readCertificates(Paths.get(url.toURI()));
        assertThat(certificates).hasSize(1);
    }

    @Test
    public void readCertificatesHandlesCertificateChain() throws Exception {
        final URL url = Resources.getResource("org/graylog2/shared/security/tls/chain.crt");
        final List<byte[]> certificates = PemReader.readCertificates(Paths.get(url.toURI()));
        assertThat(certificates).hasSize(2);
    }

    @Test(expected = CertificateException.class)
    public void readCertificatesFailsOnInvalidFile() throws Exception {
        final File file = temporaryFolder.newFile();
        PemReader.readCertificates(file.toPath());
    }

    @Test(expected = CertificateException.class)
    public void readCertificatesFailsOnDirectory() throws Exception {
        final File folder = temporaryFolder.newFolder();
        PemReader.readCertificates(folder.toPath());
    }

    @Test
    public void readPrivateKeyHandlesPrivateKey() throws Exception {
        final URL url = Resources.getResource("org/graylog2/shared/security/tls/private.key");
        final byte[] privateKey = PemReader.readPrivateKey(Paths.get(url.toURI()));
        assertThat(privateKey).isNotEmpty();
    }

    @Test
    public void readPrivateKeyHandlesSecuredPrivateKey() throws Exception {
        final URL url = Resources.getResource("org/graylog2/shared/security/tls/key-enc-pbe1.p8");
        final byte[] privateKey = PemReader.readPrivateKey(Paths.get(url.toURI()));
        assertThat(privateKey).isNotEmpty();
    }

    @Test(expected = KeyException.class)
    public void readPrivateKeyFailsOnInvalidFile() throws Exception {
        final File file = temporaryFolder.newFile();
        PemReader.readPrivateKey(file.toPath());
    }

    @Test(expected = KeyException.class)
    public void readPrivateKeyFailsOnDirectory() throws Exception {
        final File folder = temporaryFolder.newFolder();
        PemReader.readPrivateKey(folder.toPath());
    }
}
