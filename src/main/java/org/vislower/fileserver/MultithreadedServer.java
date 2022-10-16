package org.vislower.fileserver;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MultithreadedServer implements Runnable{
    private final int clientNumber;
    private final Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public MultithreadedServer(int clientNumber, Socket socket){
        this.clientNumber = clientNumber;
        this.socket = socket;
    }

    // TODO check if file or directory already exists
    @Override
    public void run() {
        // takes input from the client socket
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            short request;
            while (true){
                request = input.readShort();
                if (request == 0){
                    int count = 0;
                    while (input.readBoolean()){
                        count++;
                        receiveFile();
                    }
                    if (count == 1){
                        System.out.println("File received from client " + clientNumber);
                    }
                    else if (count > 1){
                        System.out.println("Files received from client " + clientNumber);
                    }
                } else if (request == 1) {
                    int count = 0;
                    while (input.readBoolean()){
                        count++;
                        receiveDirectory();
                    }
                    if (count == 1){
                        System.out.println("Directory received from client " + clientNumber);
                    }
                    else if (count > 1){
                        System.out.println("Directories received from client " + clientNumber);
                    }
                } else if (request == 2) {
                    // send file
                    String locationPath;
                    String destinationPath;
                    ArrayList<File> files = new ArrayList<>();
                    while (input.readBoolean()){
                        locationPath = input.readUTF();
                        File file = new File(locationPath);
                        if (file.isFile()) {
                            output.writeBoolean(true);
                            files.add(file);
                        }else {
                            output.writeBoolean(false);
                        }
                    }
                    if (!files.isEmpty()){
                        destinationPath = input.readUTF();
                        for (File f : files){
                            output.writeBoolean(true);
                            sendFile(f, f.getAbsolutePath(), destinationPath);
                        }
                        output.writeBoolean(false);
                        System.out.println("File(s) sent to client " + clientNumber);
                    }
                } else if (request == 3) {
                    // send directory
                    String locationPath;
                    String destinationPath;
                    ArrayList<File> directories = new ArrayList<>();
                    while (input.readBoolean()){
                        locationPath = input.readUTF();
                        File directory = new File(locationPath);
                        if (directory.isDirectory()) {
                            output.writeBoolean(true);
                            directories.add(directory);
                        }else {
                            output.writeBoolean(false);
                        }
                    }
                    if (!directories.isEmpty()){
                        destinationPath = input.readUTF();
                        for (File d : directories){
                            output.writeBoolean(true);
                            sendDirectory(d, destinationPath);
                        }
                        output.writeBoolean(false);
                        System.out.println("Directory(s) sent to client " + clientNumber);
                    }
                } else if (request == 4) {
                    String path;
                    while (input.readBoolean()) {
                        path = input.readUTF();
                        File file = new File(path);
                        if (file.exists()){
                            file.delete();
                            output.writeBoolean(true);
                        }
                        else {
                            output.writeBoolean(false);
                        }
                    }
                } else if (request == 5) {
                    String path;
                    while (input.readBoolean()) {
                        path = input.readUTF();
                        File directory = new File(path);
                        if (directory.isDirectory()){
                            deleteDirectory(directory);
                            output.writeBoolean(true);
                        }else {
                            output.writeBoolean(false);
                        }
                    }
                } else if (request == 6){
                    // close session
                    System.out.println("Closing session for client " + clientNumber);
                    break;
                }
            }

            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void receiveFile() {

        try {
            int bytesCount = 0;
            String parentDirectoryName = input.readUTF();
            File parentDirectory = new File(parentDirectoryName);
            if (!parentDirectory.exists()){
                parentDirectory.mkdir();
            }
            String fileName = input.readUTF(); // retrieve name of the file
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            long size = input.readLong(); // read file size
            byte[] buffer = new byte[4096];
            while (size > 0 && (bytesCount = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
                fileOutputStream.write(buffer, 0, bytesCount);
                size -= bytesCount;
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveDirectory() {
        try {
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
            throw new RuntimeException(e);
        }
    }

    private void sendFile(File file, String locationPath, String destinationPath) {
        try {
            int bytesCount = 0;
            FileInputStream fileInputStream = new FileInputStream(file);

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
            output.writeLong(file.length());

            // break file into chunks
            byte[] buffer = new byte[4096];
            while ((bytesCount = fileInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesCount);
                output.flush();
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDirectory(File directory, String destinationPath) {
        File[] files;
        String newDestinationPath = destinationPath + directory.getName() + "/";
        try {
            // send parent directory name
            output.writeUTF(destinationPath);

            output.writeUTF(newDestinationPath);
            files = directory.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    output.writeShort(0);
                    sendFile(f, f.getAbsolutePath(), newDestinationPath);
                } else if (f.isDirectory()) {
                    output.writeShort(1);
                    sendDirectory(f, newDestinationPath);
                }
            }
            output.writeShort(2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        for (File f : files){
            if (f.isFile()){
                f.delete();
            } else if (f.isDirectory()) {
                deleteDirectory(f);
                f.delete();
            }
        }
        directory.delete();
    }


}
