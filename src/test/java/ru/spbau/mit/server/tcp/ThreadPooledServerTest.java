package ru.spbau.mit.server.tcp;

public class ThreadPooledServerTest extends ServerTester {

    @Override
    public Server getServer() {
        return new ThreadPooledServer();
    }
}