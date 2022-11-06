package org.vislower.fileserver;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

// driver class
public class Main {

    public static void main(String[] args){
        try {
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
                    String keyStoreName;
                    System.out.print("Press [0] to create new keystore or [1] to load an existing keystore : ");
                    int answer = sc.nextInt();
                    if (answer == 0){
                        SecretKey secretKey = SymmetricKeyGenerator.createAESKey();
                        System.out.print("Password for the new KeyStore : ");
                        String password = br.readLine();
                        KeyStoreCreator keyStoreCreator = new KeyStoreCreator(password);
                        keyStoreCreator.createKeyStoreWithSymmetricKey(secretKey, "ClientKeyStore.jks");
                        keyStoreName = "ClientKeyStore.jks";
                    } else if (answer == 1) {
                        System.out.print("Name of the keystore you want to load : ");
                        keyStoreName = br.readLine();
                    }else {
                        System.out.println("You must use a keystore");
                        break;
                    }
                    System.out.print("Enter the local IP address of the server : ");
                    String address = br.readLine();
                    System.out.print("Enter the port on which the server is listening : ");
                    int port = sc.nextInt();
                    Client client = new Client(address, port);
                    client.execute(keyStoreName);
                    break;
                } else if (choice == 3) {
                    System.out.println("Quitting application");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR : an I/O error occured");
        } catch (InputMismatchException e) {
            System.out.println("ERROR : input is not of type int");
        } catch (NoSuchElementException e){
            System.out.println("ERROR : input is exhausted");
        } catch (IllegalStateException e) {
            System.out.println("ERROR : the scanner is closed");
        }

    }
}
