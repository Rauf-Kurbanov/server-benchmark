package ru.spbau.mit;

import ru.spbau.mit.server.tcp.SingleThreadServer;

public class ServerMain {
    public static void main(String[] args) {
        new SingleThreadServer().start(6968);
    }
}
