package ru.spbau.mit.server.tcp;

import ru.spbau.mit.server.ServerStatistics;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    protected final ServerStatistics serverStatistics = new ServerStatistics();

    public void start(int portNumber) {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(portNumber);
                // TODO
            } catch (IOException e) {
                System.err.println("Trying to run server on port " + portNumber);
                System.err.println(e.getMessage());
//                e.printStackTrace();
            }
        });
    }

    public void stop() throws IOException {
        serverThreadExecutor.shutdownNow();
    }

    // TODO runServer -> Worker
    protected abstract void runServer(int portNumber) throws IOException ;
}
