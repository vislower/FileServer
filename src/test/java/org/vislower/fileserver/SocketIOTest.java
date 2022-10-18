package org.vislower.fileserver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SocketIOTest {

    Socket socket;

    @Mock
    DataInputStream input;

    @Mock
    DataOutputStream output;

    @BeforeAll
    void setup() {
        socket = mock(Socket.class);
    }

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