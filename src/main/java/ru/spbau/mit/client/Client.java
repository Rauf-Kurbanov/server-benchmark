package ru.spbau.mit.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Client {

    protected int nQueries;
    protected int delayInMs;

    public abstract List<Integer> askToSort() throws IOException;

    public void run() throws InterruptedException, IOException {
        for (int i = 0; i < nQueries; i++) {
            askToSort();
            TimeUnit.MILLISECONDS.sleep(delayInMs);
        }
    }

    public abstract void stop() throws IOException;
}
