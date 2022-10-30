package org.vislower.fileserver;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;

public class FileEncrypter {

    private final Key secretKey;
    private final Cipher cipher;
    private final File fileToEncrypt;
    private final byte[] iv;

    public FileEncrypter(File file, Key secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        this.fileToEncrypt = file;
        this.secretKey = secretKey;
        String algorithm = "AES/CBC/PKCS5PADDING";
        this.cipher = Cipher.getInstance(algorithm);
        this.iv = getIVSecureRandom();
    }

    private byte[] getIVSecureRandom() { // generate iv with SecureRandom number
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            return iv;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getEncryptedBytesFromFile(){
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToEncrypt));
            byte[] byteFile = new byte[(int) fileToEncrypt.length()];
            bufferedInputStream.read(byteFile);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(byteFile); // encrypt bytes from file

            byte[] encryptedByteFile = new byte[(int) (encryptedBytes.length + iv.length)];
            System.arraycopy(iv, 0, encryptedByteFile, 0, iv.length);// put iv at the beginning of the array
            System.arraycopy(encryptedBytes,0 ,encryptedByteFile ,iv.length ,encryptedBytes.length);// then put encrypted bytes
            return encryptedByteFile;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IOException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

}
