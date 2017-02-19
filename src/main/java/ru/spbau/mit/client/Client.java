package ru.spbau.mit.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Client {

    protected int nQueries;
    protected int delayInMs;

    public abstract List<Integer> askToSort() throws IOException;

    public List<List<Integer>> run() throws InterruptedException, IOException {
        final List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < nQueries; i++) {
            res.add(askToSort());
        }
        return res;
    }

    public abstract void stop() throws IOException;
}
