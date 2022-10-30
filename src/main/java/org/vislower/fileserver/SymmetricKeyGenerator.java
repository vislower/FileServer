package org.vislower.fileserver;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SymmetricKeyGenerator {
    // create symmetric AES 256 bits key from a SecureRandom number
    public static SecretKey createAESKey() {
        SecureRandom securerandom = new SecureRandom();

        KeyGenerator keygenerator;
        try {
            keygenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        keygenerator.init(256, securerandom);

        return keygenerator.generateKey();
    }
}
