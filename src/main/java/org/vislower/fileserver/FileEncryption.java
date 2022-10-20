package org.vislower.fileserver;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class FileEncryption {

    private final SecretKey secretKey;
    private final Cipher cipher;
    private final String algorithm = "AES/CBC/PKCS5PADDING";
    private final byte[] toEncrypt;

    public FileEncryption(byte[] toEncrypt, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.toEncrypt = toEncrypt;
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(algorithm);
    }

    private byte[] getIVSecureRandom() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            return iv;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[][] encrypt(){
        try {
            byte[] iv = getIVSecureRandom();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(toEncrypt);
            byte[][] info = {iv, encrypted};
            return info;
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

}
