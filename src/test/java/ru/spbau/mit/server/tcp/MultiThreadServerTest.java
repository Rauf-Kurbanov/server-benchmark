package ru.spbau.mit.server.tcp;

public class MultiThreadServerTest extends ServerTester {
    @Override
    public Server getServer() {
        return new MultiThreadServer();
    }
}