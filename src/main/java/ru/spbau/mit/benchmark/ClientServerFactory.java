package ru.spbau.mit.benchmark;

import lombok.RequiredArgsConstructor;
import ru.spbau.mit.client.Client;
import ru.spbau.mit.client.SingleTcpClient;
import ru.spbau.mit.client.TcpClient;
import ru.spbau.mit.client.UdpClient;
import ru.spbau.mit.runner.ServerArchitecture;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.tcp.NonBlockingServer;
import ru.spbau.mit.server.tcp.SingleThreadServer;
import ru.spbau.mit.server.tcp.ThreadPooledServer;
import ru.spbau.mit.server.tcp.async.AsynchronousServer;
import ru.spbau.mit.server.udp.UdpSingleThreadServer;

import java.io.IOException;
import java.net.InetAddress;

@RequiredArgsConstructor
public class ClientServerFactory {

    private final ServerArchitecture serverArchitectures;

    public Client getClient(InetAddress serverAddress, int port, BenchmarkParameters bp) throws IOException, InterruptedException {
        switch (serverArchitectures) {
            case TCP_ASYNCHRONOUS:
                return new TcpClient(serverAddress, port, bp);
            case TCP_NON_BLOCKING:
                return new TcpClient(serverAddress, port, bp);
            case TCP_SIGNLE_THREAD:
                return new SingleTcpClient(serverAddress, port, bp);
            case TCP_THREAD_POOLED:
                return new TcpClient(serverAddress, port, bp);
            case UDP_SINGLE_THREAD:
                return new UdpClient(serverAddress, port, bp);
            case UDP_THREADPOOLED:
                return new UdpClient(serverAddress, port, bp);
        }
        return null;
    }

    public Server getServer() {
        switch (serverArchitectures) {
            case TCP_ASYNCHRONOUS:
                return new AsynchronousServer();
            case TCP_NON_BLOCKING:
                return new NonBlockingServer();
            case TCP_SIGNLE_THREAD:
                return new SingleThreadServer();
            case TCP_THREAD_POOLED:
                return new ThreadPooledServer();
            case UDP_SINGLE_THREAD:
                return new UdpSingleThreadServer();
            case UDP_THREADPOOLED:
                return new ru.spbau.mit.server.udp.ThreadPooledServer();
        }
        return null;
    }
}
