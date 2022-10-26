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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeyStoreCreatorTest {

    private final String password = "test";
    private KeyStore keyStoreTest = null;

    @BeforeAll
    void setup() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStoreCreator keyStoreCreator = new KeyStoreCreator(password);
        keyStoreTest = keyStoreCreator.createKeyStoreWithSymmetricKey();
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

    @AfterAll
    void cleanUp() {
        File keyStore = new File("ClientKeystore.jks");
        keyStore.delete();
    }



}