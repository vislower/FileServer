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
    private Cipher cipher;
    private final byte[] encryptedBytesFile;
    private final byte[] iv = new byte[16];


    public FileDecrypter(byte[] encryptedBytesFile, Key secretKey){
        this.encryptedBytesFile = encryptedBytesFile;
        this.secretKey = secretKey;
        String algorithm = "AES/CBC/PKCS5PADDING";
        try {
            this.cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : no such algorithm");
        } catch (NoSuchPaddingException e) {
            System.out.println("ERROR : no such padding");
        }
    }

    public byte[] getDecryptedBytes() {
        byte[] encryptedBytes = new byte[(encryptedBytesFile.length - iv.length)];
        System.arraycopy(encryptedBytesFile, 0, iv, 0, iv.length); // get iv from file
        System.arraycopy(encryptedBytesFile, iv.length, encryptedBytes, 0, encryptedBytes.length); // get actual encrypted bytes
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv)); // decrypt
        } catch (InvalidKeyException e) {
            System.out.println("ERROR : key is inappropriate for initializing this cipher");
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println("ERROR : the given algorithm parameters are inappropriate for this cipher");
        }
        try {
            return cipher.doFinal(encryptedBytes);
        } catch (IllegalBlockSizeException e) {
            System.out.println("ERROR : illegal block size");
        } catch (BadPaddingException e) {
            System.out.println("ERROR : bad padding");
        }
        return null;
    }
}
