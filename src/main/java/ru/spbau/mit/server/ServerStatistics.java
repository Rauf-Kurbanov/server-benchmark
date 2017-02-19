package ru.spbau.mit.server;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServerStatistics {
    private final AtomicLong queryTimeSum = new AtomicLong(0);
    private final AtomicLong clientTimeSum = new AtomicLong(0);
    private final AtomicInteger nQueries = new AtomicInteger(0);

    public void pushStatistics(ServerTimestamp serverTimestamp) {
        queryTimeSum.addAndGet(serverTimestamp.getRequestProcessingTime());
        clientTimeSum.addAndGet(serverTimestamp.getClientProcessingTime());
        nQueries.incrementAndGet();
    }

    public void clear() {
        queryTimeSum.set(0);
        clientTimeSum.set(0);
        nQueries.set(0);
    }

    public long getQueryProcessingMetric() {
        return queryTimeSum.get() / nQueries.get();
    }

    public long getClientProcessingMetric() {
        return clientTimeSum.get() / nQueries.get();
    }
}
