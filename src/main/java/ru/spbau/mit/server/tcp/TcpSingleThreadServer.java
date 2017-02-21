package ru.spbau.mit.server.tcp;

import ru.spbau.mit.server.RequestAnswerer;
import ru.spbau.mit.server.Server;
import ru.spbau.mit.server.ServerTimestamp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpSingleThreadServer extends Server {
    private ServerSocket serverSocket;
    private final RequestAnswerer requestAnswerer = new RequestAnswerer();

    @Override
    protected void runServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();
                    final ServerTimestamp st = requestAnswerer.answerServerQuery(clientSocket);
                    serverStatistics.pushStatistics(st);
            } catch (IOException e) {
                System.out.println("Cannot open client socket");
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
    }
}
