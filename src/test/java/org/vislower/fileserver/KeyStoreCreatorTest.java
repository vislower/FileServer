package org.vislower.fileserver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeyStoreCreatorTest {

    private final String password = "1234";
    SecretKey symmetricKey = SymmetricKeyGenerator.createAESKey();

    @BeforeAll
    void setup() {
        KeyStoreCreator keyStoreCreator = new KeyStoreCreator(password);
        keyStoreCreator.createKeyStoreWithSymmetricKey(symmetricKey, "src/test/resources/KeyStoreTest.jks");
    }

    @Test
    void checkIfKeyStoreIsNotNull() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/test/resources/KeyStoreTest.jks"), password.toCharArray());
        assertNotNull(ks);
    }

    @Test
    void whenEntryIsMissingOrOfIncorrectType_thenReturnsNull() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/test/resources/KeyStoreTest.jks"), password.toCharArray());
        assertNull(ks.getKey("some-other-entry", password.toCharArray()));
        assertNotNull(ks.getKey("FileEncryptionAESKey", password.toCharArray()));
    }

    @Test
    void whenWantingToAccessKeyWithIncorrectPassword_thenThrowsException() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/test/resources/KeyStoreTest.jks"), password.toCharArray());
        assertThrows(UnrecoverableKeyException.class, () -> ks.getKey("FileEncryptionAESKey", "5678".toCharArray()));
    }

    @Test
    void whenAddingAlias_thenCanQueryByType() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/test/resources/KeyStoreTest.jks"), password.toCharArray());
        assertTrue(ks.containsAlias("FileEncryptionAESKey"));
        assertFalse(ks.containsAlias("other-alias"));
    }

    @Test
    void testIfKeyIsNotTempered() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/test/resources/KeyStoreTest.jks"), password.toCharArray());
        Key retrievedKey = ks.getKey("FileEncryptionAESKey", password.toCharArray());
        byte[] rawSymmetricKey = symmetricKey.getEncoded();
        String rawSymmetricKeyAsString = Base64.getEncoder().encodeToString(rawSymmetricKey);
        byte[] rawRetrievedKey = retrievedKey.getEncoded();
        String rawRetrievedKeyAsString = Base64.getEncoder().encodeToString(rawRetrievedKey);
        assertEquals(rawRetrievedKeyAsString, rawSymmetricKeyAsString);
    }
}