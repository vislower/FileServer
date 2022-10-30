package org.vislower.fileserver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    int testPort = 5000;
    public Server serverTest;
    public ServerSocket serverSocketTest;

    @BeforeAll
    void setup() throws IOException {
        serverTest = new Server(testPort);
        serverSocketTest = serverTest.createServerSocket();
    }

    @Test
    void testIfSocketServerGetsCreated() {
        assertNotNull(serverSocketTest);
    }

    @Test
    void testIfServerSocketListensOnCorrectPort() {
        assertEquals(serverSocketTest.getLocalPort(), testPort);
    }

    @Test
    void testIfClientSocketGetsCreated() throws IOException {
        ServerSocket mockServerSocket = mock(ServerSocket.class);
        when(mockServerSocket.accept()).thenReturn(new Socket());
        assertNotNull(serverTest.createClientSocket(mockServerSocket));
    }

}