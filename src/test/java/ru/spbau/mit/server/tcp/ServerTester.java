package ru.spbau.mit.server.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.client.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class ServerTester {

    private static final int DEFAULT_SERVER_PORT = 5342;

    protected int getServerPort() {
        return DEFAULT_SERVER_PORT;
    }

    public abstract Server getServer();

    private Server server;

    @Before
    public void init() throws IOException, InterruptedException {
        server = getServer();
        server.start(getServerPort());
        Thread.sleep(1000);
    }

    @After
    public void after() throws InterruptedException, IOException {
        server.stop();
        Thread.sleep(1000);
    }

//    @Ignore
    @Test
    public void casualTest() throws InterruptedException, IOException {
        final int arrSize = 10;
        Thread.sleep(500);
        final Client client = new Client(getServerPort(), arrSize);
        assertSorted(client.askToSort());
        client.stop();
    }

//    @Ignore
    @Test
    public void largeArray() throws IOException {
        final int arrSize = 100_000;
        final Client client = new Client(getServerPort(), arrSize);
        assertSorted(client.askToSort());
        client.stop();
    }

    private static void assertSorted(List<Integer> original) {
        final List<Integer> expected = new ArrayList<>(original);
        Collections.sort(expected);
        assertEquals("List is not sorted", expected, original);
    }
}
