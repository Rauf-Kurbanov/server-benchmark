package ru.spbau.mit.benchmark;

import ru.spbau.mit.client.Client;

import java.net.InetAddress;

public interface ServerArchitectureDescription {
    String getName();

//    Protocol getProtocol();

    int getDefaultServerPort();

    Client getClient(InetAddress serverAddress, int port, BenchmarkParameters parameters);
}
