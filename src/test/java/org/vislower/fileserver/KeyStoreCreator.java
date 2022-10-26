package org.vislower.fileserver;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

public class KeyStoreCreator {

    private final KeyStore keyStore;
    private final char[] keyStorePassword;

    public KeyStoreCreator(String keyStorePassword) throws KeyStoreException {
        this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    public void initKeyStore() throws CertificateException, IOException, NoSuchAlgorithmException {
        keyStore.load(null, keyStorePassword);
    }

    public void saveKeyStore() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        FileOutputStream fileOutputStream = new FileOutputStream("ClientKeystore.jks");
        keyStore.store(fileOutputStream, keyStorePassword);
    }
    public void saveSymmetricKey(String alias, SecretKey symmetricKey) throws KeyStoreException {
        KeyStore.SecretKeyEntry secretKey = new KeyStore.SecretKeyEntry(symmetricKey);
        KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(keyStorePassword);
        keyStore.setEntry(alias, secretKey, password);
    }

    public static void main(String[] args) throws IOException {
        SecretKey secretKey = SymmetricKeyGenerator.createAESKey();
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);


        System.out.print("Password for the new KeyStore : ");
        String password = br.readLine();

        try {
            KeyStoreCreator keyStoreCreator = new KeyStoreCreator(password);
            keyStoreCreator.initKeyStore();
            keyStoreCreator.saveKeyStore();
            System.out.println(Base64.getEncoder().encodeToString(secretKey.getEncoded()));

            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("ClientKeystore.jks"), "1234".toCharArray());
            KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter pass = new KeyStore.PasswordProtection("1234".toCharArray());
            ks.setEntry("a", secret, pass);

            Key symmetric = ks.getKey("a", "1234".toCharArray());
            System.out.println(Base64.getEncoder().encodeToString(symmetric.getEncoded()));
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
