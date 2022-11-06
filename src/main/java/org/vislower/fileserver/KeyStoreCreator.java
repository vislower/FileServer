package org.vislower.fileserver;

import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreCreator {

    private KeyStore keyStore;
    private final char[] keyStorePassword;

    public KeyStoreCreator(String keyStorePassword) {
        try {
            this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            System.out.println("ERROR : no Provider supports a KeyStoreSpi implementation for the specified type");
        } catch (NullPointerException e) {
            System.out.println("ERROR : KeyStore type is null");
        }
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    public void createKeyStoreWithSymmetricKey(SecretKey symmetricKey, String path) {
        try {
            keyStore.load(null, keyStorePassword);
        } catch (IOException e) {
            System.out.println("ERROR : incorrect password or an I/O or format problem with the keystore data occured");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : the algorithm used to check the integrity of the keystore cannot be found");
        } catch (CertificateException e) {
            System.out.println("ERROR : one or more certificates in the keystore could not be loaded");
        }
        // create new entry for symmetric key
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(symmetricKey);
        KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyStorePassword);
        try {
            keyStore.setEntry("FileEncryptionAESKey", secretKeyEntry, entryPassword);
        } catch (KeyStoreException e) {
            System.out.println("ERROR : keystore has not been initialized (loaded)");
        } catch (NullPointerException e){
            System.out.println("ERROR : alias or entry is null");
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : file not found");
        }
        try {
            keyStore.store(fileOutputStream, keyStorePassword); // save keystore
        } catch (KeyStoreException e) {
            System.out.println("ERROR : keystore has not been initialized (loaded)");
        } catch (IOException e) {
            System.out.println("ERROR : there was an I/O problem with data");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : the appropriate data integrity algorithm could not be found");
        } catch (CertificateException e) {
            System.out.println("ERROR : one or more certificates in the keystore could not be loaded");
        }
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }
}
