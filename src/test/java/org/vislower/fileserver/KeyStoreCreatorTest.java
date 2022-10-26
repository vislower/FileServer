package org.vislower.fileserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeyStoreCreatorTest {

    private final String password = "test";
    SecretKey symmetricKey = SymmetricKeyGenerator.createAESKey();
    private KeyStore keyStoreTest = null;

    @BeforeAll
    void setup() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStoreCreator keyStoreCreator = new KeyStoreCreator(password);
        keyStoreTest = keyStoreCreator.createKeyStoreWithSymmetricKey(symmetricKey);
    }

    @Test
    void checkIfKeyStoreIsNotNull() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        assertNotNull(keyStoreTest);
    }

    @Test
    void whenEntryIsMissingOrOfIncorrectType_thenReturnsNull() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        assertNull(keyStoreTest.getKey("some-other-entry", password.toCharArray()));
        assertNotNull(keyStoreTest.getKey("FileEncryptionAESKey", password.toCharArray()));
    }

    @Test
    void whenWantingToAccessKeyWithIncorrectPassword_thenThrowsException() {
        assertThrows(UnrecoverableKeyException.class, () -> keyStoreTest.getKey("FileEncryptionAESKey", "1234".toCharArray()));
    }

    @Test
    void whenAddingAlias_thenCanQueryByType() throws KeyStoreException {
        assertTrue(keyStoreTest.containsAlias("FileEncryptionAESKey"));
        assertFalse(keyStoreTest.containsAlias("other-alias"));
    }

    @Test
    void testIfKeyIsNotTempered() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        Key retrievedKey = keyStoreTest.getKey("FileEncryptionAESKey", password.toCharArray());
        byte[] rawSymmetricKey = symmetricKey.getEncoded();
        String rawSymmetricKeyAsString = Base64.getEncoder().encodeToString(rawSymmetricKey);
        byte[] rawRetrievedKey = retrievedKey.getEncoded();
        String rawRetrievedKeyAsString = Base64.getEncoder().encodeToString(rawRetrievedKey);
        assertEquals(rawRetrievedKeyAsString, rawSymmetricKeyAsString);
    }

    @AfterAll
    void cleanUp() {
        File keyStore = new File("ClientKeystore.jks");
        keyStore.delete();
    }



}