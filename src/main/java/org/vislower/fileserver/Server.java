package org.vislower.fileserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int clientNumber = 0;

    private final int port;

    public Server(int serverPort) {
        port = serverPort;
    }

    public void startServer() {
        System.out.println("Server started");
        ServerSocket serverSocket;
        serverSocket = createServerSocket();
        if (serverSocket != null){
            while(true) {
                System.out.println("Waiting for client ...");

                // server listening
                Socket socket = createClientSocket(serverSocket);
                if (socket != null){
                    clientNumber ++;
                    System.out.println("Client " + clientNumber + " accepted");

                    // MULTITHREADING
                    MultithreadedServer ms = new MultithreadedServer(clientNumber, socket);
                    Thread thread = new Thread(ms);
                    thread.start();
                }
            }
        }
    }

    public ServerSocket createServerSocket() {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occurred when opening the socket");
        } catch (IllegalArgumentException e){
            System.out.println("ERROR : illegal port number");
        }
        return null;
    }

    public Socket createClientSocket(ServerSocket serverSocket){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occurred when waiting for a connection");
        }
        return null;
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.startServer();
    }
}
