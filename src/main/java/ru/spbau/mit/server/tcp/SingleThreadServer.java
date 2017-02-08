package ru.spbau.mit.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadServer implements Server {
    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private ServerSocket serverSocket;
    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    private synchronized void runServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();
                requestAnswerer.answerServerQuery(clientSocket);
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
        serverThreadExecutor.shutdownNow();
    }
}
