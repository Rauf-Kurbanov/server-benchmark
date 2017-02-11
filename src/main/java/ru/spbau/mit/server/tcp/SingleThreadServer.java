package ru.spbau.mit.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadServer extends Server {
//    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private ServerSocket serverSocket;
    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    @Override
    protected void runServer(int portNumber) throws IOException {
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
    public void stop() throws IOException {
        super.stop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
//        serverThreadExecutor.shutdownNow();
    }
}
