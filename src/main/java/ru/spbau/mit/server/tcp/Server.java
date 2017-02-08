package ru.spbau.mit.server.tcp;

import java.io.IOException;

public interface Server {

    // TODO synchronized?
    // TODO add default implementation
    void start(int portNumber) throws IOException;

    void stop() throws IOException;
}
