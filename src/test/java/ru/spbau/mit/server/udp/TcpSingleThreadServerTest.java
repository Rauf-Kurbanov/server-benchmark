package ru.spbau.mit.server.udp;

import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.UdpClient;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.ServerTester;

import java.io.IOException;
import java.net.InetAddress;

public class TcpSingleThreadServerTest extends ServerTester {

    @Override
    public Server getServer() {
        return new UdpSingleThreadServer();
    }

    @Override
    protected Client getClient(InetAddress serverAddress, int port, int arraySize
            , int delayInMs, int nQueries) throws IOException {
        return new UdpClient(serverAddress, port, arraySize, delayInMs, nQueries);
    }
}