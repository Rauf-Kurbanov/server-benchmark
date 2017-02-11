package ru.spbau.mit.server.tcp;

import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.TcpClient;

import java.io.IOException;
import java.net.InetAddress;

public class NonBlockingServerTest extends ServerTester {

    @Override
    protected Server getServer() {
        return new NonBlockingServer();
    }

    @Override
    protected Client getClient(InetAddress serverAddress, int port, int arraySize
            , int delayInMs, int nQueries) throws IOException {
        return new TcpClient(serverAddress, port, arraySize, delayInMs, nQueries);
    }

//    @Override
//    protected BiFunction<Integer, Integer, Client> getClientCreator() {
//        return TcpClient::new;
//    }
}