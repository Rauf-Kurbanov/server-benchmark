package ru.spbau.mit.server.tcp.async;

import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.TcpClient;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.ServerTester;

import java.io.IOException;
import java.net.InetAddress;

public class AsynchronousServerTest extends ServerTester {

    @Override
    public Server getServer() {
        return new AsynchronousServer();
    }

    @Override
    protected Client getClient(InetAddress serverAddress, int port, int arraySize
            , int delayInMs, int nQueries) throws IOException, InterruptedException {
        return new TcpClient(serverAddress, port, arraySize, delayInMs, nQueries);
    }
}