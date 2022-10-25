package org.vislower.fileserver;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileDecrypter {

    private final SecretKey secretKey;
    private final Cipher cipher;
    private final String algorithm = "AES/CBC/PKCS5PADDING";
    private final byte[] encryptedBytesFile;
    private final byte[] iv = new byte[16];


    public FileDecrypter(byte[] encryptedBytesFile, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.encryptedBytesFile = encryptedBytesFile;
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(algorithm);
    }

    public byte[] getDecryptedBytes() {
        try {
            byte[] encryptedBytes = new byte[(encryptedBytesFile.length - iv.length)];
            System.arraycopy(encryptedBytesFile, 0, iv, 0, iv.length);
            System.arraycopy(encryptedBytesFile, iv.length, encryptedBytes, 0, encryptedBytes.length);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return decryptedBytes;
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
