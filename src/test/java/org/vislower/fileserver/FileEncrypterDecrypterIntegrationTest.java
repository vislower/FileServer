package org.vislower.fileserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileEncrypterDecrypterIntegrationTest {

    // This test uses the java-developers-guide.pdf in the resources folder of the test folder as a test file.

    @Test
    void whenEncryptingFileAndThenDecrypting_thenOriginalFileIsReturned() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        File fileToEncrypt = new File("src/test/resources/java-developers-guide.pdf");
        SecretKey secretKey = SymmetricKeyGenerator.createAESKey();

        FileEncrypter fileEncrypter = new FileEncrypter(fileToEncrypt, secretKey);
        byte[] encryptedFileAsBytes = fileEncrypter.getEncryptedBytesFromFile();

        FileDecrypter fileDecrypter = new FileDecrypter(encryptedFileAsBytes, secretKey);
        byte[] decryptedFileAsBytes = fileDecrypter.getDecryptedBytes();

        File decryptedFile = new File("src/test/resources/java-developers-guide-2.pdf");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(decryptedFile));
        bufferedOutputStream.write(decryptedFileAsBytes);

        assertEquals(Files.mismatch(fileToEncrypt.toPath(), decryptedFile.toPath()), -1);
        bufferedOutputStream.close();
    }

    @Test
    void whenEncryptingFileAndThenDecryptingWithKeyFromKeyStore_thenOriginalFileIsReturned() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        File fileToEncrypt = new File("src/test/resources/java-developers-guide.pdf");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/test/resources/KeyStoreTest.jks"), "1234".toCharArray());
        Key secretKey = ks.getKey("FileEncryptionAESKey", "1234".toCharArray());

        FileEncrypter fileEncrypter = new FileEncrypter(fileToEncrypt, secretKey);
        byte[] encryptedFileAsBytes = fileEncrypter.getEncryptedBytesFromFile();

        FileDecrypter fileDecrypter = new FileDecrypter(encryptedFileAsBytes, secretKey);
        byte[] decryptedFileAsBytes = fileDecrypter.getDecryptedBytes();

        File decryptedFile = new File("src/test/resources/java-developers-guide-2.pdf");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(decryptedFile));
        bufferedOutputStream.write(decryptedFileAsBytes);

        assertEquals(Files.mismatch(fileToEncrypt.toPath(), decryptedFile.toPath()), -1);
        bufferedOutputStream.close();
    }

    @AfterAll
    void cleanUp() {
        File decryptedFile = new File("src/test/resources/java-developers-guide-2.pdf");
        decryptedFile.delete();
    }

}