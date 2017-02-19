package ru.spbau.mit;

import ru.spbau.mit.runner.ServerRunner;

public class ServerMain {

    private static ServerRunner serverRunner = new ServerRunner();

    public static void main(String[] args) {
        serverRunner.start();
    }
}
