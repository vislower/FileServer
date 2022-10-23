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

    public FileEncrypter(File file, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        this.fileToEncrypt = file;
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

    // TODO : break this method in smaller method
    private void encryptFile(){
        try {
            FileInputStream fileInputStream = new FileInputStream(fileToEncrypt);
            byte[] bFile = new byte[(int) fileToEncrypt.length()];
            fileInputStream.read(bFile);
            fileInputStream.close();

            byte[] iv = getIVSecureRandom();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            String fileToEncryptName = fileToEncrypt.getAbsolutePath();
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; fileToEncryptName.charAt(i) != '.'; i++){
                stringBuilder.append(fileToEncryptName.charAt(i));
            }
            stringBuilder.append(".enc");
            String encryptedFileName = stringBuilder.toString();

            FileOutputStream fileOutputStream = new FileOutputStream(encryptedFileName);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);
            fileOutputStream.write(iv);
            cipherOutputStream.write(bFile);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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
        File file = new File("/home/admin/client/fox.png");
        FileEncrypter fileEncrypter = new FileEncrypter(file, createAESKey());
        fileEncrypter.encryptFile();
    }

}
