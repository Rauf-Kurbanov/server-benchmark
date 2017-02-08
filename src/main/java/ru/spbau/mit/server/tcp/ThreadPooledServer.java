package ru.spbau.mit.server.tcp;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class ThreadPooledServer implements Server {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    // why synchronized?
    private void runServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();
                executor.execute(() -> {
                    try {
                        requestAnswerer.answerServerQuery(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                System.out.println("Cannot open client socket");
            }
        }
    }

    @Override
    public void start(int portNumber) {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(portNumber);
            } catch (IOException e) {
                throw new RuntimeException("Can't start server", e);
            }
        });
    }

    @Override
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
        executor.shutdownNow();
        serverThreadExecutor.shutdownNow();
    }
}
