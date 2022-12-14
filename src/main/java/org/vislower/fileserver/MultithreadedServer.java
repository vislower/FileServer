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

    @Override
    public void run() {
        // takes input from the client socket
        try {
            input = SocketIO.createInputStream(socket);
            output = SocketIO.createOutputStream(socket);

            short request;
            while (true){
                request = input.readShort();
                if (request == 0){
                    receiveFilesFromClient();
                } else if (request == 1) {
                    receiveDirectoriesFromClient();
                } else if (request == 2) {
                    sendFilesToClient();
                } else if (request == 3) {
                    sendDirectoriesToClient();
                } else if (request == 4) {
                    deleteFilesFromClientRequest();
                } else if (request == 5) {
                    deleteDirectoriesFromClientRequest();
                } else if (request == 6){
                    System.out.println("Closing session for client " + clientNumber);
                    break;
                }
            }

            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }

    }

    private void receiveFilesFromClient() throws IOException {
        int count = 0;
        while (input.readBoolean()){
            count++; // count number of files received
            receiveFile();
        }
        if (count > 0){
            System.out.println("File(s) received from client " + clientNumber);
        }
    }

    private void receiveDirectoriesFromClient() throws IOException {
        int count = 0;
        while (input.readBoolean()){
            count++; // count number of directories received
            receiveDirectory();
        }
        if (count > 0){
            System.out.println("Directory(s) received from client " + clientNumber);
        }
    }

    private void sendFilesToClient() throws IOException {
        String locationPath;
        String destinationPath;
        ArrayList<File> files = new ArrayList<>();
        while (input.readBoolean()){ // receive files as long as client sends them
            locationPath = input.readUTF();
            File file = new File(locationPath);
            if (file.isFile()) {
                output.writeBoolean(true);
                files.add(file);
            }else {
                output.writeBoolean(false);
            }
        }
        if (!files.isEmpty()){ // check if server has to send files
            destinationPath = input.readUTF();
            for (File f : files){
                output.writeBoolean(true);
                sendFile(f, destinationPath);
            }
            output.writeBoolean(false);
            System.out.println("File(s) sent to client " + clientNumber);
        }
    }

    private void sendDirectoriesToClient() throws IOException {
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
    }

    private void deleteFilesFromClientRequest() throws IOException {
        String path;
        while (input.readBoolean()) {
            path = input.readUTF();
            File file = new File(path);
            output.writeBoolean(file.delete());
        }
    }

    private void deleteDirectoriesFromClientRequest() throws IOException {
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
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            long size = input.readLong(); // read file size
            byte[] buffer = new byte[4096];
            while (size > 0 && (bytesCount = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
                fileOutputStream.write(buffer, 0, bytesCount);
                size -= bytesCount;
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : file not found");
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
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
            System.out.println("ERROR : an I/O error occured");
        }
    }

    private void sendFile(File file, String destinationPath) {
        try {
            int bytesCount;
            FileInputStream fileInputStream = new FileInputStream(file);

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
            output.writeLong(file.length());

            // break file into chunks
            byte[] buffer = new byte[4096];
            while ((bytesCount = fileInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesCount);
                output.flush();
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : file not found");
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        }
    }

    private void sendDirectory(File directory, String destinationPath) {
        File[] files;
        String newDestinationPath = destinationPath + directory.getName() + "/";
        try {
            // send parent directory name, so it can be created if it already doesn't exist on the client
            output.writeUTF(destinationPath);

            output.writeUTF(newDestinationPath); // new path with the directory that will be sent
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

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files){
                if (f.isFile()){
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteDirectory(f);
                    f.delete();
                }
            }
        }
        directory.delete();
    }


}
