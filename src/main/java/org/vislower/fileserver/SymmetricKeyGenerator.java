package org.vislower.fileserver;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SymmetricKeyGenerator {
    // create symmetric AES 256-bit key from a SecureRandom number
    public static SecretKey createAESKey() {
        SecureRandom securerandom = new SecureRandom();

        KeyGenerator keygenerator = null;
        try {
            keygenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : Provider does not support a KeyGeneratorSpi implementation for the specified algorithm");
        } catch (NullPointerException e){
            System.out.println("ERROR : algorithm is null");
        }

        if (keygenerator != null) {
            try {
                keygenerator.init(256, securerandom);
            } catch (InvalidParameterException e){
                System.out.println("ERROR : keysize is wrong or not supported");
            }
        }

        if (keygenerator != null) {
            return keygenerator.generateKey();
        }
        return null;
    }
}
