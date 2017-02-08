package ru.spbau.mit.server.tcp.async;

import ru.spbau.mit.server.tcp.Server;
import ru.spbau.mit.server.tcp.ServerTester;

public class AsynchronousServerTest extends ServerTester {

    @Override
    public Server getServer() {
        return new AsynchronousServer();
    }
}