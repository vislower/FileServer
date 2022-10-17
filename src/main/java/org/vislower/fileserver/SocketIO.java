package org.vislower.fileserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketIO {

    public static DataInputStream createInputStream(Socket socket) throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    public static DataOutputStream createOutputStream(Socket socket) throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }
}
