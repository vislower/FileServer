package org.vislower.fileserver;

import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;


class FileEncrypterDecrypterIntegrationTest {

    // This test uses as a test the java-developers-guide.pdf in the resources folder of the test folder.

    @Test
    void whenEncryptingFileAndThenDecrypting_thenOriginalFileIsReturned() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        File fileToEncrypt = new File("src/test/resources/java-developers-guide.pdf");
        SecretKey secretKey = FileEncrypter.createAESKey();

        FileEncrypter fileEncrypter = new FileEncrypter(fileToEncrypt, secretKey);
        byte[] encryptedFileAsBytes = fileEncrypter.getEncryptedBytesFromFile();

        FileDecrypter fileDecrypter = new FileDecrypter(encryptedFileAsBytes, secretKey);
        byte[] decryptedFileAsBytes = fileDecrypter.getDecryptedBytes();

        File decryptedFile = new File("src/test/resources/java-developers-guide-2.pdf");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(decryptedFile));
        bufferedOutputStream.write(decryptedFileAsBytes);

        assertEquals(Files.mismatch(fileToEncrypt.toPath(), decryptedFile.toPath()), -1);
    }

}