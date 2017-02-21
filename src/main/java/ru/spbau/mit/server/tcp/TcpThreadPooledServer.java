package ru.spbau.mit.server.tcp;

import lombok.RequiredArgsConstructor;
import ru.spbau.mit.server.RequestAnswerer;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.ServerTimestamp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class TcpThreadPooledServer extends Server {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    @Override
    protected void runServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();
                executor.execute(() -> {
                    try {
                        while (!clientSocket.isClosed()) {
                            final ServerTimestamp st = requestAnswerer.answerServerQuery(clientSocket);
                            serverStatistics.pushStatistics(st);
                        }
                    } catch (IOException ignored) {
                    }
                });
            } catch (IOException e) {
                System.err.println("Can't answer server request");
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void shutdown() throws IOException {
        super.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
        executor.shutdownNow();
    }
}
