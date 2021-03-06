package ru.spbau.mit.server.tcp;

import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.SingleTcpClient;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.ServerTester;

import java.io.IOException;
import java.net.InetAddress;

public class TcpSingleThreadServerTest extends ServerTester {

    @Override
    protected Server getServer() {
        return new TcpSingleThreadServer();
    }

    @Override
    protected Client getClient(InetAddress serverAddress, int port, int arraySize
            , int delayInMs, int nQueries) throws IOException {
        return new SingleTcpClient(serverAddress, port, arraySize, delayInMs, nQueries);
    }
}