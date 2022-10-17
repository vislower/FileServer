package org.vislower.fileserver;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SocketIOTest {

    @Mock
    Socket socket;

    @Mock
    DataInputStream input;

    @Mock
    DataOutputStream output;

    @Test
    void testIfDataInputStreamGetsCreated() throws IOException {
        when(socket.getInputStream()).thenReturn(input);
        assertNotNull(SocketIO.createInputStream(socket));
    }

    @Test
    void testIfDataOutputStreamGetsCreated() throws IOException {
        when(socket.getOutputStream()).thenReturn(output);
        assertNotNull(SocketIO.createOutputStream(socket));
    }

}