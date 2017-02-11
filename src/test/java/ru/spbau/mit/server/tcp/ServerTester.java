package ru.spbau.mit.server.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class ServerTester {

// beforeclass afterclass
//    private static final int DEFAULT_SERVER_PORT = 5342;
    private static final int DEFAULT_SERVER_PORT = 6968;

    protected int getServerPort() {
        return DEFAULT_SERVER_PORT;
    }

    protected abstract Server getServer();

    protected abstract Client getClient(InetAddress serverAddress, int port, int arraySize, int delayInMs, int nQueries) throws IOException;
//    @FunctionalInterface
//    public interface CientFactory {
//        Client getClient(InetAddress serverAddress, int portNumber, int n);
//    }
//
//    protected CientFactory cientFactory;

    private Server server;
//    private final CientFactory clientCreator = (CientFactory) getCientFactory();

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
//        final TcpClient client = new TcpClient(getServerPort(), arrSize);
//        final UdpClient client = new UdpClient(getServerPort(), arrSize);
//        final Client client = clientCreator.apply(getServerPort(), arrSize);
//        final Client client = clientCreator.getClient(InetAddress.getByName("localhost"), getServerPort(), arrSize);
        final Client client = getClient(InetAddress.getByName("localhost"), getServerPort(), arrSize
                , 0, 0);
        assertSorted(client.askToSort());
//        Thread.sleep(5000);
//        client.stop();
    }

//    @Ignore
    @Test
    public void largeArray() throws IOException {
        final int arrSize = 10_000;
//        final TcpClient client = new TcpClient(getServerPort(), arrSize);
//        final Client client = clientCreator.apply(getServerPort(), arrSize);
        final Client client = getClient(InetAddress.getByName("localhost"), getServerPort(), arrSize
                , 0, 0);
//        final Client client = clientCreator.getClient(InetAddress.getByName("localhost"), getServerPort(), arrSize);
        assertSorted(client.askToSort());
        client.stop();
    }

    private static void assertSorted(List<Integer> original) {
        final List<Integer> expected = new ArrayList<>(original);
        Collections.sort(expected);
        assertEquals("List is not sorted", expected, original);
    }
}
