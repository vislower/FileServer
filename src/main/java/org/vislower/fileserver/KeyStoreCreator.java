package org.vislower.fileserver;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(symmetricKey);
        KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyStorePassword);
        keyStore.setEntry("FileEncryptionAESKey", secretKeyEntry, entryPassword);
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        keyStore.store(fileOutputStream, keyStorePassword);
        fileOutputStream.close();
    }

    public static void main(String[] args) throws IOException {
        SecretKey secretKey = SymmetricKeyGenerator.createAESKey();

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        System.out.print("Password for the new KeyStore : ");
        String password = br.readLine();

        try {
            KeyStoreCreator keyStoreCreator = new KeyStoreCreator(password);
            keyStoreCreator.createKeyStoreWithSymmetricKey(secretKey, "ClientKeyStore.jks");
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
