package org.vislower.fileserver;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class FileEncrypter {

    private final SecretKey secretKey;
    private final Cipher cipher;
    private final String algorithm = "AES/CBC/PKCS5PADDING";
    private final File fileToEncrypt;
    private final byte[] iv;

    public FileEncrypter(File file, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        this.fileToEncrypt = file;
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(algorithm);
        this.iv = getIVSecureRandom();
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

    private byte[] getEncryptedBytesFromFile(){
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToEncrypt));
            byte[] byteFile = new byte[(int) fileToEncrypt.length()];
            bufferedInputStream.read(byteFile);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(byteFile);

            byte[] encryptedByteFile = new byte[(int) (encryptedBytes.length + iv.length)];
            System.arraycopy(iv, 0, encryptedByteFile, 0, iv.length);
            System.arraycopy(encryptedBytes,0 ,encryptedByteFile ,iv.length ,encryptedBytes.length);

            return encryptedByteFile;

        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO : delete this method
    private static SecretKey createAESKey() {
        SecureRandom securerandom = new SecureRandom();

        KeyGenerator keygenerator;
        try {
            keygenerator = KeyGenerator.getInstance("AES"); // return KeyGenerator object that generates secret keys for the specified algorithm
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        keygenerator.init(256, securerandom); // Initializes this key generator for a certain keysize, using a user-provided source of randomness.

        SecretKey key = keygenerator.generateKey();

        return key;
    }

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        File file = new File("/home/admin/client/BashDoc.pdf");
        FileEncrypter fileEncrypter = new FileEncrypter(file, createAESKey());
        byte[] b = fileEncrypter.getEncryptedBytesFromFile();
        FileOutputStream fileOutputStream = new FileOutputStream(new File("/home/admin/BashDoc.enc"));
        fileOutputStream.write(b);
    }

}
