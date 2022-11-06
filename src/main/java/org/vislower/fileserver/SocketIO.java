package org.vislower.fileserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketIO {

    public static DataInputStream createInputStream(Socket socket){
        try {
            return new DataInputStream(socket.getInputStream());
        }catch (IOException e) {
            System.out.println("ERROR : socket is closed/not connected or an I/O error occured when creating the input stream");
        }
        return null;
    }

    public static DataOutputStream createOutputStream(Socket socket){
        try {
            return new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("ERROR : socket is closed/not connected or an I/O error occured when creating the input stream");
        }
        return null;
    }
}
