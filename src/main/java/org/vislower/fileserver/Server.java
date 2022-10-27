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

    private void startServer() {
        System.out.println("Server started");
        ServerSocket serverSocket;
        try {
            serverSocket = createServerSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while(true) {
            try {
                System.out.println("Waiting for client ...");

                // server listening
                Socket socket = createClientSocket(serverSocket);
                clientNumber ++;
                System.out.println("Client " + clientNumber + " accepted");

                // MULTITHREADING
                MultithreadedServer ms = new MultithreadedServer(clientNumber, socket);
                Thread thread = new Thread(ms);
                thread.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ServerSocket createServerSocket() throws IOException {
        return new ServerSocket(port);
    }

    public Socket createClientSocket(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.startServer();
    }
}
