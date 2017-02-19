package ru.spbau.mit.server.tcp.async;

import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.tcp.async.handlers.AcceptHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousServer extends Server {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private volatile AsynchronousServerSocketChannel serverChannel;

    @Override
    protected void runServer(int portNumber) throws IOException {
        serverChannel = AsynchronousServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(portNumber), Integer.MAX_VALUE);
        serverChannel.accept(serverChannel, new AcceptHandler(serverStatistics));
    }

    @Override
    public void shutdown() throws IOException {
        serverThreadExecutor.shutdown();
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
}
