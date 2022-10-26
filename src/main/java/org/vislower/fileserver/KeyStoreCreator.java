package org.vislower.fileserver;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.SQLOutput;
import java.util.Base64;

public class KeyStoreCreator {

    private final KeyStore keyStore;
    private final char[] keyStorePassword;

    public KeyStoreCreator(String keyStorePassword) throws KeyStoreException {
        this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    public KeyStore createKeyStoreWithSymmetricKey(SecretKey symmetricKey) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        initKeyStore();
        saveKeyStore();
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("ClientKeystore.jks"), keyStorePassword);
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(symmetricKey);
        KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyStorePassword);
        keyStore.setEntry("FileEncryptionAESKey", secretKeyEntry, entryPassword);
        return keyStore;
    }

    public void initKeyStore() throws CertificateException, IOException, NoSuchAlgorithmException {
        keyStore.load(null, keyStorePassword);
    }

    public void saveKeyStore() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        FileOutputStream fileOutputStream = new FileOutputStream("ClientKeystore.jks");
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
            KeyStore keyStore = keyStoreCreator.createKeyStoreWithSymmetricKey(secretKey);
            FileOutputStream fileOutputStream = new FileOutputStream("ClientKeystore.jks");
            keyStore.store(fileOutputStream, password.toCharArray());
            fileOutputStream.close();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
