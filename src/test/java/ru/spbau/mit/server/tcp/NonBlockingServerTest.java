package ru.spbau.mit.server.tcp;

public class NonBlockingServerTest extends ServerTester {

    @Override
    public Server getServer() {
        return new NonBlockingServer();
    }
}