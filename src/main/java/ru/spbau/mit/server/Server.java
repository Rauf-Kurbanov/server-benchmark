package ru.spbau.mit.server;

import ru.mit.spbau.FlyingDataProtos;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    protected final ServerStatistics serverStatistics = new ServerStatistics();

    public FlyingDataProtos.BenchmarkResult getBenchmarkResult() {
        final FlyingDataProtos.BenchmarkResult res =  FlyingDataProtos.BenchmarkResult.newBuilder()
                .setQueryProcessingTime(serverStatistics.getQueryProcessingMetric())
                .setClientProcessingTime(serverStatistics.getClientProcessingMetric())
                .build();
        serverStatistics.clear();
        return res;
    }

    public void start(int portNumber) {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(portNumber);
            } catch (IOException ignored) {}
        });
    }

    public void shutdown() throws IOException {
        serverThreadExecutor.shutdownNow();
    }

    protected abstract void runServer(int portNumber) throws IOException ;
}
