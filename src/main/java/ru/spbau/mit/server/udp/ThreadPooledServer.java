package ru.spbau.mit.server.udp;

import ru.spbau.mit.server.RequestAnswerer;
import ru.spbau.mit.server.Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer extends Server {
    private static final int RECEIVE_BUFFER_SIZE = 1024 * 1024 * 100;
    private static final int SEND_BUFFER_SIZE = 1024 * 1024 * 100;
    private volatile DatagramSocket socket;

    private final RequestAnswerer requestAnswerer = new RequestAnswerer();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    protected void runServer(int portNumber) throws IOException {
        socket = new DatagramSocket(portNumber);
        socket.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
        socket.setSendBufferSize(SEND_BUFFER_SIZE);

        while (!socket.isClosed()) {
            executor.execute(() -> {
                try {
                    serverStatistics.pushStatistics(requestAnswerer.answerServerQuery(socket));
                } catch (IOException ignored) {}
            });
        }
    }

    @Override
    public void shutdown() throws IOException {
        super.shutdown();
        socket.close();
    }
}
