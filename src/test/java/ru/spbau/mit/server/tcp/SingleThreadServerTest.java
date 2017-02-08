package ru.spbau.mit.server.tcp;

public class SingleThreadServerTest extends ServerTester {

    @Override
    public Server getServer() {
        return new SingleThreadServer();
    }
}