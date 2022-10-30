package org.vislower.fileserver;

import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreCreator {

    private final KeyStore keyStore;
    private final char[] keyStorePassword;

    public KeyStoreCreator(String keyStorePassword) throws KeyStoreException {
        this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    public void createKeyStoreWithSymmetricKey(SecretKey symmetricKey, String path) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        keyStore.load(null, keyStorePassword);
        // create new entry for symmetric key
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(symmetricKey);
        KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyStorePassword);
        keyStore.setEntry("FileEncryptionAESKey", secretKeyEntry, entryPassword);
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        keyStore.store(fileOutputStream, keyStorePassword); // save keystore
        fileOutputStream.close();
    }
}
