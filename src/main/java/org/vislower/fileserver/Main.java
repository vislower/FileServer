package org.vislower.fileserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

// driver class
public class Main {

    public static void main(String[] args) throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Welcome to the FileServer application");
        while (true){
            Scanner sc = new Scanner(System.in);
            System.out.println("Press:\n[1] to start the serverside application\n[2] to start the clientside application\n[3] to quit");
            int choice = sc.nextInt();
            if (choice == 1){
                System.out.print("Enter port on which the server must listen (it must not be used) : ");
                int port = sc.nextInt();
                Server server = new Server(port);
                server.startServer();
                break;
            } else if (choice == 2) {
                System.out.print("Enter the local IP address of the server : ");
                String address = br.readLine();
                System.out.print("Enter the port on which the server is listening : ");
                int port = sc.nextInt();
                Client client = new Client(address, port);
                client.execute();
                break;
            } else if (choice == 3) {
                System.out.println("Quitting application");
                break;
            }
        }
    }
}
