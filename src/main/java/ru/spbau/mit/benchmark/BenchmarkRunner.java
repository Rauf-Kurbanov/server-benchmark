package ru.spbau.mit.benchmark;

import lombok.RequiredArgsConstructor;
import ru.spbau.mit.client.Client;

import javax.enterprise.inject.spi.Producer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class BenchmarkRunner {
    private final int nClients;
    private final Producer<Client> clientProducer;

//    private final BenchmarkParameters myParameters;
//    private final InetAddress myServerAddress;
//    private final int port;

//    InetAddress getAddress() {
//        return myServerAddress;
//    }

//    ServerArchitectureDescription getArchitectureDescription() {
//        return myDescription;
//    }

    public long start() throws BrokenBarrierException, InterruptedException {
//        final int clientsCount = myParameters.getNClients();
        final CyclicBarrier barrier = new CyclicBarrier(nClients + 1);
        final Thread[] clients = new Thread[nClients];

        final AtomicLong averageTimePerClient = new AtomicLong(0);
        for (int i = 0; i < nClients; i++) {
//            final Client client = BenchmarkClient.create(myParameters, myDescription, myServerAddress);
//            final Client client = BenchmarkParameters.getClient(myServerAddress, port, myParameters);
            final Client client = clientProducer.produce(null);
            final Thread clientThread = new Thread(() -> {
                try {
                    barrier.await();
                    final long clientStartTime = System.nanoTime();
                    client.run();
                    long duration = System.nanoTime() - clientStartTime;
                    // TODO hz
//                    final long sleepMs = myParameters.getDelayInMs() * (myParameters.getNQueries() - 1);
//                    duration -= TimeUnit.MILLISECONDS.toNanos(sleepMs);
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

        return averageTimePerClient.get();
    }
}
