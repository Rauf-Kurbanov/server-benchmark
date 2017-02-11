package ru.spbau.mit.server.udp;

import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.UdpClient;
import ru.spbau.mit.server.tcp.Server;
import ru.spbau.mit.server.tcp.ServerTester;

import java.io.IOException;
import java.net.InetAddress;

public class ThreadPooledServerTest extends ServerTester {

    @Override
    protected Server getServer() {
        return new ThreadPooledServer();
    }

    @Override
    protected Client getClient(InetAddress serverAddress, int port, int arraySize
            , int delayInMs, int nQueries) throws IOException {
        return new UdpClient(serverAddress, port, arraySize, delayInMs, nQueries);
    }
}