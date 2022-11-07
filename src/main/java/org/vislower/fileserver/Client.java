package org.vislower.fileserver;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Scanner;


public class Client {

    private DataOutputStream output;
    private DataInputStream input;
    private final Socket socket;
    private static Key symmetricKey;

    public Client(String address, int port){
        socket = createClientSocket(address, port);
        if (socket != null) {
            System.out.println("You are connected to the file server");
            output = SocketIO.createOutputStream(socket);
            input = SocketIO.createInputStream(socket);
        }
    }

    public void execute(String keyStoreName){
        try {
            unlockKeyStore(keyStoreName);
            while (true){
                Scanner sc = new Scanner(System.in);
                System.out.println("\nWelcome to the file transfer server enter :\n[0] to send files\n[1] to send directories\n[2] to request files\n[3] to request directories\n[4] to delete a file on the server\n[5] to delete a directory from the server\n[6] to quit");
                int answer = sc.nextInt();
                if (answer == 0){
                    sendFilesToServer();
                } else if (answer == 1) {
                    sendDirectoriesToServer();
                } else if (answer == 2) {
                    requestFilesFromServer();
                } else if (answer == 3) {
                    requestDirectoriesFromServer();
                } else if (answer == 4) {
                    requestFilesDeletionFromServer();
                } else if (answer == 5) {
                    requestDirectoriesDeletionFromServer();
                } else if (answer == 6) {
                    System.out.println("Quitting the program");
                    output.writeShort(6);
                    break;
                }else {
                    System.out.println("You must choose between the choices");
                }
            }
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }

    public Socket createClientSocket(String address, int port){
        try {
            return new Socket(address, port);
        } catch (UnknownHostException e){
            System.out.println("ERROR : the IP address of the host could not be determined");
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured when creating the socket");
        }catch (IllegalArgumentException e) {
            System.out.println("ERROR : port is out of range");
        }
        return null;
    }

    private static void unlockKeyStore(String keyStoreName) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        System.out.print("Password to unlock the keystore : ");
        String password = null;
        try {
            password = br.readLine();
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            System.out.println("ERROR : no Provider supports a KeyStoreSpi implementation for the specified type");
        } catch (NullPointerException e) {
            System.out.println("ERROR : KeyStore type is null");
        }
        try {
            if (ks != null) {
                if (password != null) {
                    ks.load(new FileInputStream(keyStoreName), password.toCharArray());
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR : incorrect password or an I/O or format problem with the keystore data occured");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : the algorithm used to check the integrity of the keystore cannot be found");
        } catch (CertificateException e) {
            System.out.println("ERROR : one or more certificates in the keystore could not be loaded");
        }
        try {
            if (ks != null) {
                if (password != null) {
                    symmetricKey = ks.getKey("FileEncryptionAESKey", password.toCharArray());
                }
            }
        } catch (KeyStoreException e) {
            System.out.println("ERROR : the keystore has not been initialized (loaded)");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR : the algorithm for recovering the key cannot be found");
        } catch (UnrecoverableKeyException e) {
            System.out.println("ERROR : the key cannot be recovered, it may be due to a wrong password input");
        }
    }

    private void sendFilesToServer() throws IOException {
        output.writeShort(0);

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String locationPath;
        ArrayList<File> files = new ArrayList<>();
        while (true) {
            System.out.println("Enter the path of the file you want to send, press [ENTER] when you have finished :  ");
            locationPath = br.readLine();
            if (locationPath.equals("")){
                break;
            }else {
                File file = new File(locationPath);
                if (file.isFile()){
                    files.add(file);
                }else {
                    System.out.println("This file does not exist");
                }
            }
        }

        if (files.isEmpty()){
            output.writeBoolean(false);
            System.out.println("You haven't specified any valid files to be sent");
        }
        else {
            System.out.println("Enter the path where you want to send the file(s), if you send the file(s) in a directory that does not exist, it will be created :  ");
            String destinationPath = br.readLine();
            if (destinationPath.charAt(destinationPath.length()-1) != '/'){
                destinationPath += "/";
            }

            for (File f : files){
                output.writeBoolean(true);
                sendFile(f, destinationPath);
            }
            output.writeBoolean(false);
            System.out.println("File(s) sent");
        }
    }

    private void sendDirectoriesToServer() throws IOException {
        output.writeShort(1); // so servers knows we are sending a directory

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String locationPath;
        ArrayList<File> files = new ArrayList<>();
        while (true) {
            System.out.println("Enter the path of the directory you want to send, press [ENTER] when you have finished :  ");
            locationPath = br.readLine();
            if (locationPath.equals("")){
                break;
            }else {
                File directory = new File(locationPath);
                if (directory.isDirectory()){
                    files.add(directory);
                }else {
                    System.out.println("This directory does not exist");
                }
            }
        }

        if (files.isEmpty()){
            output.writeBoolean(false);
            System.out.println("You haven't specified any valid directories to be sent");
        }
        else {
            System.out.println("Enter the path where you want to send the directories :  ");
            String destinationPath = br.readLine();
            if (destinationPath.charAt(destinationPath.length()-1) != '/'){
                destinationPath += "/";
            }

            for (File d : files){
                output.writeBoolean(true);
                sendDirectory(d, destinationPath);
            }
            output.writeBoolean(false);
            System.out.println("Directory(s) sent");
        }
    }

    private void requestFilesFromServer() throws IOException {
        output.writeShort(2);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        boolean exists;
        int requestAmount = 0;
        int invalidRequestAmount = 0;
        while (true){
            System.out.println("Enter the path of the file you want to retrieve, press [ENTER] when you are finished :  ");
            String locationPath = br.readLine();
            if (locationPath.equals("")){
                output.writeBoolean(false);
                break;
            }
            else {
                requestAmount++;
                output.writeBoolean(true);
                // check if exists
                output.writeUTF(locationPath);
                exists = input.readBoolean();
                if (!exists){
                    invalidRequestAmount++;
                    System.out.println("There is no file with this name on the server");
                }
            }
        }

        if (requestAmount != invalidRequestAmount){
            System.out.println("Enter the path where you want to receive the file(s), if you retrieve the file(s) in a directory that does not exist, it will be created  :  ");
            String destinationPath = br.readLine();
            if (destinationPath.charAt(destinationPath.length()-1) != '/'){
                destinationPath += "/";
            }

            output.writeUTF(destinationPath);
            while (input.readBoolean()){
                receiveFile();
            }
            System.out.println("File(s) received from the server");
        }else {
            System.out.println("You have not specified any valid file(s)");
        }
    }

    private void requestDirectoriesFromServer() throws IOException {
        output.writeShort(3);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        boolean exists;
        int requestAmount = 0;
        int invalidRequestAmount = 0;
        while (true){
            System.out.println("Enter the path of the directory you want to retrieve, press [ENTER] when you are finished :  ");
            String locationPath = br.readLine();
            if (locationPath.equals("")){
                output.writeBoolean(false);
                break;
            }
            else {
                requestAmount++;
                output.writeBoolean(true);
                // check if exists
                output.writeUTF(locationPath);
                exists = input.readBoolean();
                if (!exists){
                    invalidRequestAmount++;
                    System.out.println("There is no directory with this name on the server");
                }
            }
        }

        if (requestAmount != invalidRequestAmount){
            System.out.println("Enter the path where you want to receive the directory(s), if you retrieve the directory(s) in a directory that does not exist, it will be created  :  ");
            String destinationPath = br.readLine();
            if (destinationPath.charAt(destinationPath.length()-1) != '/'){
                destinationPath += "/";
            }

            output.writeUTF(destinationPath);
            while (input.readBoolean()){
                receiveDirectory();
            }
            System.out.println("Directory(s) received from the server");
        }else {
            System.out.println("You have not specified any valid directory(s)");
        }
    }

    private void requestFilesDeletionFromServer() throws IOException {
        output.writeShort(4);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String locationPath;
        while (true){
            System.out.println("Enter the path of the file you want to delete on the server, press [ENTER] when you are finished :  ");
            locationPath = br.readLine();
            if (locationPath.equals("")){
                output.writeBoolean(false);
                break;
            }
            else {
                output.writeBoolean(true);
                output.writeUTF(locationPath);
                boolean success = input.readBoolean();
                if (success) {
                    System.out.println("File deleted from server");
                }else {
                    System.out.println("File does not exist on server");
                }
            }
        }
    }

    private void requestDirectoriesDeletionFromServer() throws IOException {
        output.writeShort(5);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String locationPath;
        while (true){
            System.out.println("Enter the path of the directory you want to delete on the server, press [ENTER] when you are finished :  ");
            locationPath = br.readLine();

            if (locationPath.equals("")){
                output.writeBoolean(false);
                break;
            }
            else {
                output.writeBoolean(true);
                output.writeUTF(locationPath);
                boolean success = input.readBoolean();
                if (success) {
                    System.out.println("Directory deleted from server");
                }else {
                    System.out.println("Directory does not exist on server");
                }
            }
        }
    }

    private void sendFile(File file, String destinationPath) {
        try {
            int bytesCount;
            // get encrypted bytes from file to be sent
            FileEncrypter fileEncrypter = new FileEncrypter(file, symmetricKey);
            byte[] encryptedBytes = fileEncrypter.getEncryptedBytesFromFile();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encryptedBytes);

            String locationPath = file.getAbsolutePath();
            // retrieve name of the file
            StringBuilder sb = new StringBuilder();
            for (int i = locationPath.length() - 1; i >= 0; i--) {
                if (locationPath.charAt(i) == '/') {
                    break;
                } else {
                    sb.append(locationPath.charAt(i));
                }
            }
            String fileName = sb.reverse().toString();
            fileName = destinationPath + fileName;

            // send parent directory name
            output.writeUTF(destinationPath);

            // send name of the file
            output.writeUTF(fileName);

            // send file size
            output.writeLong(encryptedBytes.length);

            // break file into chunks
            byte[] buffer = new byte[4096];
            while ((bytesCount = byteArrayInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesCount);
                output.flush();
            }
            byteArrayInputStream.close();
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }

    private void sendDirectory(File directory, String destinationPath) {
        File[] files;
        String newDestinationPath = destinationPath + directory.getName() + "/";
        try {
            // send parent directory name
            output.writeUTF(destinationPath);

            output.writeUTF(newDestinationPath); // so the server can create the directory
            files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        output.writeShort(0);
                        sendFile(f, newDestinationPath);
                    } else if (f.isDirectory()) {
                        output.writeShort(1);
                        sendDirectory(f, newDestinationPath);
                    }
                }
            }
            output.writeShort(2);
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }

    private void receiveFile() {

        try {
            int bytesCount;
            String parentDirectoryName = input.readUTF();
            File parentDirectory = new File(parentDirectoryName);
            if (!parentDirectory.exists()){
                parentDirectory.mkdir();
            }
            String fileName = input.readUTF(); // retrieve name of the file
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            long size = input.readLong(); // read file size
            byte[] buffer = new byte[4096];
            while (size > 0 && (bytesCount = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){ // read file by chunks
                byteArrayOutputStream.write(buffer, 0, bytesCount);
                size -= bytesCount;
            }
            byte[] encryptedBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            FileDecrypter fileDecrypter = new FileDecrypter(encryptedBytes, symmetricKey);
            byte[] decryptedBytes = fileDecrypter.getDecryptedBytes();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName)); // create file
            bufferedOutputStream.write(decryptedBytes);
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : file not found");
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }

    private void receiveDirectory() {
        try {
            // makes sure parent directory exist
            String parentDirectoryName = input.readUTF();
            File parentDirectory = new File(parentDirectoryName);
            if (!parentDirectory.exists()){
                parentDirectory.mkdir();
            }
            // create directory
            String directoryPath = input.readUTF();
            File directory = new File(directoryPath);
            if (!directory.exists()){
                directory.mkdir();
            }
            short type;
            while (true){
                type = input.readShort();
                if (type == 0){
                    receiveFile();
                } else if (type == 1) {
                    receiveDirectory();
                } else if (type == 2) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5000);
        client.execute("ClientKeyStore.jks");

    }
}
