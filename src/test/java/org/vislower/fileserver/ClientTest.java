package org.vislower.fileserver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientTest {
    int testPort = 9001;
    String testAddress = "127.0.0.1";
    public Client clientTest;
    public Server serverTest;
    public ServerSocket serverSocketTest;
    public Socket clientSocketTest;

    @BeforeAll
    void setup() throws IOException {
        serverTest = new Server(testPort);
        serverSocketTest = serverTest.createServerSocket();
        clientTest = new Client(testAddress, testPort);
        clientSocketTest = clientTest.createClientSocket(testAddress, testPort);
    }

    @Test
    void testIfClientSocketGetsCreated() {
        assertNotNull(clientSocketTest);
    }

    @Test
    void testIfClientSocketIsOnCorrectPort() {
        assertEquals(clientSocketTest.getPort(), testPort);
    }

}