package ru.spbau.mit.benchmark;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.mit.runner.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.BrokenBarrierException;

public class ExperimentTest {

    private static final ServerRunner serverRunner = new ServerRunner();

    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        serverRunner.start();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        serverRunner.stop();
    }

    @Test
    public void testFinishes() throws IOException, BrokenBarrierException, InterruptedException {
        experimentOnce(ServerArchitecture.TCP_ASYNCHRONOUS, VaryingParameter.ELEMENTS_PER_REQ);
    }

    @Test
    public void testFinishesTwice() throws BrokenBarrierException, InterruptedException, IOException {
        for (int i = 0; i < 2; i++) {
            experimentOnce(ServerArchitecture.TCP_ASYNCHRONOUS, VaryingParameter.ELEMENTS_PER_REQ);
        }
    }

    @Test
    public void testNonBlockingFinishes() throws IOException, BrokenBarrierException, InterruptedException {
        experimentOnce(ServerArchitecture.TCP_NON_BLOCKING, VaryingParameter.ELEMENTS_PER_REQ);
    }

    @Test
    public void testAllArchitecturesFinishes() throws IOException, BrokenBarrierException, InterruptedException {
        for (ServerArchitecture sa : ServerArchitecture.values()) {
            experimentOnce(sa, VaryingParameter.ELEMENTS_PER_REQ);
        }
    }

    private void experimentOnce(ServerArchitecture sa, VaryingParameter vp) throws InterruptedException, IOException, BrokenBarrierException {
        final RunnerClient rc = new RunnerClient(InetAddress.getLocalHost());
        rc.run(sa);
        Thread.sleep(500);

        final BenchmarkParameters bp = new BenchmarkParameters(15, 5, 10, 100);
        final BenchmarkRunner br = new BenchmarkRunner(new ClientServerFactory(sa), InetAddress.getLocalHost());

        final int from = 10;
        final int to = 100;
        final int step = 20;
        final Experiment experiment = new Experiment(br, bp, rc, vp, from, to, step);

        experiment.run();

        rc.stop();
    }
}