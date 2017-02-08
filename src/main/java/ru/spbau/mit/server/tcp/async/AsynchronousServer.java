package ru.spbau.mit.server.tcp.async;

import ru.spbau.mit.server.tcp.Server;
import ru.spbau.mit.server.tcp.async.handlers.AcceptHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousServer implements Server {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private volatile AsynchronousServerSocketChannel serverChannel;

    @Override
    public void start(int portNumber) {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(portNumber);
                // TODO
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void runServer(int portNumber) throws IOException {
        serverChannel = AsynchronousServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(portNumber), Integer.MAX_VALUE);
        serverChannel.accept(serverChannel, new AcceptHandler());
    }

    @Override
    public void stop() throws IOException {
        serverThreadExecutor.shutdown();
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
}
