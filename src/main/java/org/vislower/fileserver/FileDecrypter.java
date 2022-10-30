package org.vislower.fileserver;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class FileDecrypter {

    private final Key secretKey;
    private final Cipher cipher;
    private final byte[] encryptedBytesFile;
    private final byte[] iv = new byte[16];


    public FileDecrypter(byte[] encryptedBytesFile, Key secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.encryptedBytesFile = encryptedBytesFile;
        this.secretKey = secretKey;
        String algorithm = "AES/CBC/PKCS5PADDING";
        this.cipher = Cipher.getInstance(algorithm);
    }

    public byte[] getDecryptedBytes() {
        try {
            byte[] encryptedBytes = new byte[(encryptedBytesFile.length - iv.length)];
            System.arraycopy(encryptedBytesFile, 0, iv, 0, iv.length); // get iv from file
            System.arraycopy(encryptedBytesFile, iv.length, encryptedBytes, 0, encryptedBytes.length); // get actual encrypted bytes
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv)); // decrypt
            return cipher.doFinal(encryptedBytes);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
