package org.vislower.fileserver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreCreator {

    private final KeyStore keyStore;
    private final char[] keyStorePassword;

    public KeyStoreCreator(String keyStoreName, String keyStorePassword) throws KeyStoreException {
        this.keyStore = KeyStore.getInstance("pkcs12");
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    private void initKeyStore() throws CertificateException, IOException, NoSuchAlgorithmException {
        keyStore.load(null, keyStorePassword);
    }

    private void saveKeyStore() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        FileOutputStream fileOutputStream = new FileOutputStream("ClientKeystore.p12");
        keyStore.store(fileOutputStream, keyStorePassword);
    }
}
