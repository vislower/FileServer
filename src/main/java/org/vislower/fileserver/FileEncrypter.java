package org.vislower.fileserver;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;

public class FileEncrypter {

    private final Key secretKey;
    private Cipher cipher;
    private final File fileToEncrypt;
    private final byte[] iv;

    public FileEncrypter(File file, Key secretKey) {
        this.fileToEncrypt = file;
        this.secretKey = secretKey;
        String algorithm = "AES/CBC/PKCS5PADDING";
        try {
            this.cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : no such algorithm");
        } catch (NoSuchPaddingException e) {
            System.out.println("ERROR : no such padding");
        }
        this.iv = getIVSecureRandom();
    }

    private byte[] getIVSecureRandom() { // generate iv with SecureRandom number
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            return iv;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : no such algorithm");
        }
        return null;
    }

    public byte[] getEncryptedBytesFromFile(){
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToEncrypt));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : file not found");
        }
        byte[] byteFile = new byte[(int) fileToEncrypt.length()];
        try {
            if (bufferedInputStream != null) {
                bufferedInputStream.read(byteFile);
            }
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        } catch (InvalidKeyException e) {
            System.out.println("ERROR : key is inappropriate for initializing this cipher");
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println("ERROR : the given algorithm parameters are inappropriate for this cipher");
        }
        byte[] encryptedBytes = new byte[0]; // encrypt bytes from file
        try {
            encryptedBytes = cipher.doFinal(byteFile);
        } catch (IllegalBlockSizeException e) {
            System.out.println("ERROR : illegal block size");
        } catch (BadPaddingException e) {
            System.out.println("ERROR : bad padding");
        }

        byte[] encryptedByteFile = new byte[(int) (encryptedBytes.length + iv.length)];
        try {
            System.arraycopy(iv, 0, encryptedByteFile, 0, iv.length);// put iv at the beginning of the array
            System.arraycopy(encryptedBytes,0 ,encryptedByteFile ,iv.length ,encryptedBytes.length);// then put encrypted bytes
            return encryptedByteFile;
        } catch (IndexOutOfBoundsException e){
            System.out.println("ERROR : index out of bound");
        } catch (ArrayStoreException e){
            System.out.println("ERROR : type mismatch between the two arrays");
        } catch (NullPointerException e){
            System.out.println("ERROR : one or both of the two arrays is null");
        }
        return null;
    }

}
