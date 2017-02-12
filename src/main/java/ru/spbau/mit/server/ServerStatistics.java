package ru.spbau.mit.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerStatistics {
    private final List<Long> queryProcessingTimes = Collections.synchronizedList(new ArrayList<>());
    private final List<Long> clientProcessingTimes = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger ai = new AtomicInteger(0);

    public void pushStatistics(ServerTimestamp serverTimestamp) {
        queryProcessingTimes.add(serverTimestamp.getRequestProcessingTime());
        clientProcessingTimes.add(serverTimestamp.getClientProcessingTime());
        System.out.println(ai.getAndIncrement() + " | " + serverTimestamp.getRequestProcessingTime()
                + " | " + serverTimestamp.getClientProcessingTime());
    }

    public void clear() {
        clientProcessingTimes.clear();
        queryProcessingTimes.clear();
    }

    public long getQueryProcessingMetric() {
        return average(queryProcessingTimes);
    }

    public long getClientProcessingMetric() {
        return average(clientProcessingTimes);
    }

    public ServerTimestamp getProcessingMetric() {
        return new ServerTimestamp(average(queryProcessingTimes), average(clientProcessingTimes));
    }

    private static long average(List<Long> longs) {
        final int size = longs.size();
        if (size == 0) {
            return Long.MAX_VALUE;
        }

        long sum = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            sum += longs.get(i);
        }

        return sum / size;
    }
}
