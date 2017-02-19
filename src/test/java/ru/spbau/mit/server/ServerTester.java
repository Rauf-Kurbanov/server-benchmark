package ru.spbau.mit.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.benchmark.BenchmarkParameters;
import ru.spbau.mit.client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class ServerTester {

    private static final int DEFAULT_SERVER_PORT = 6968;
    protected int getServerPort() {
        return DEFAULT_SERVER_PORT;
    }
    protected abstract Server getServer();
    protected abstract Client getClient(InetAddress serverAddress, int port, int arraySize, int delayInMs, int nQueries) throws IOException, InterruptedException;
    private Server server;

    @Before
    public void init() throws IOException, InterruptedException {
        server = getServer();
        server.start(getServerPort());
        Thread.sleep(1000);
    }

    @After
    public void after() throws InterruptedException, IOException {
        server.shutdown();
        Thread.sleep(1000);
    }

    @Test
    public void casualTest() throws InterruptedException, IOException {
        final int arrSize = 10;
        Thread.sleep(500);
        final Client client = getClient(InetAddress.getLocalHost(), getServerPort(), arrSize
                , 0, 0);
        assertSorted(client.askToSort());
    }

    @Test
    public void largeArray() throws IOException, InterruptedException {
        final int arrSize = 10_000;
        final Client client = getClient(InetAddress.getLocalHost(), getServerPort(), arrSize
                , 0, 0);
        assertSorted(client.askToSort());
        client.stop();
    }

    @Test
    public void oneClientOneQuery() throws IOException, InterruptedException {
        final BenchmarkParameters bp = new BenchmarkParameters(10, 1, 1, 100);
        final Client client = getClient(InetAddress.getLocalHost(), getServerPort(), bp.getArraySize(),
                bp.getDelayInMs(), bp.getNQueries());
        final List<List<Integer>> res = client.run();
        for (List<Integer> list : res) {
            assertSorted(list);
        }
    }

    @Test
    public void oneClientFiveQueries() throws IOException, InterruptedException {
        final BenchmarkParameters bp = new BenchmarkParameters(10, 1, 5, 100);
        final Client client = getClient(InetAddress.getLocalHost(), getServerPort(), bp.getArraySize(),
                bp.getDelayInMs(), bp.getNQueries());
        final List<List<Integer>> res = client.run();
        assertEquals(5, res.size());
        for (List<Integer> list : res) {
            assertSorted(list);
        }
    }

    @Test
    public void twoClientsFiveQueries() throws IOException, InterruptedException {
        final BenchmarkParameters bp = new BenchmarkParameters(10, 2, 5, 100);
        final Client clientA = getClient(InetAddress.getLocalHost(), getServerPort(), bp.getArraySize(),
                bp.getDelayInMs(), bp.getNQueries());
        final Client clientB = getClient(InetAddress.getLocalHost(), getServerPort(), bp.getArraySize(),
                bp.getDelayInMs(), bp.getNQueries());

        final List<List<Integer>> resA = clientA.run();
        assertEquals(5, resA.size());
        for (List<Integer> list : resA) {
            assertSorted(list);
        }

        final List<List<Integer>> resB = clientB.run();
        assertEquals(5, resB.size());
        for (List<Integer> list : resB) {
            assertSorted(list);
        }
    }

    protected static void assertSorted(List<Integer> original) {
        final List<Integer> expected = new ArrayList<>(original);
        Collections.sort(expected);
        assertEquals("List is not sorted", expected, original);
    }
}
