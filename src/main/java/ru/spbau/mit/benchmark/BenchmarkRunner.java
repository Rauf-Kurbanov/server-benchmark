package ru.spbau.mit.benchmark;

import lombok.RequiredArgsConstructor;
import ru.spbau.mit.client.Client;
import ru.spbau.mit.runner.RunnerProtocol;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class BenchmarkRunner {
    private final ClientServerFactory clientServerFactory;
    private final InetAddress serverAddress;

    public long start(BenchmarkParameters bp) throws BrokenBarrierException, InterruptedException, IOException {
        final int nClients = bp.getNClients();
        final CyclicBarrier barrier = new CyclicBarrier(nClients + 1);
        final Thread[] clients = new Thread[nClients];

        final AtomicLong averageTimePerClient = new AtomicLong(0);
        for (int i = 0; i < nClients; i++) {
            final Client client = clientServerFactory.getClient(serverAddress, RunnerProtocol.TESTED_SERVER_PORT, bp);
            final Thread clientThread = new Thread(() -> {
                try {
                    barrier.await();
                    final long clientStartTime = System.nanoTime();
                    client.run();
                    long duration = System.nanoTime() - clientStartTime;
                    client.stop();
                    averageTimePerClient.addAndGet(duration / nClients);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            clientThread.start();
            clients[i] = clientThread;
        }

        barrier.await();
        for (Thread thread : clients) {
            thread.join();
        }
        return averageTimePerClient.get() / 1_000_000;
    }
}
