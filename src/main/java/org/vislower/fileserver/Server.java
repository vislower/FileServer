package org.vislower.fileserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private DataInputStream input;
    private DataOutputStream output;
    private ServerSocket serverSocket;
    private Socket socket;

    private int clientNumber = 0;

    public Server(int port){
        // starts server and waits for a connection
        System.out.println("Server started");
        while(true) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Waiting for client ...");

                // server listening
                socket = serverSocket.accept();
                clientNumber ++;
                System.out.println("Client " + clientNumber + " accepted");

                // MULTITHREADING
                MultithreadedServer ms = new MultithreadedServer(clientNumber, socket);
                Thread thread = new Thread(ms);
                thread.start();

                // close socket server
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
    }
}
