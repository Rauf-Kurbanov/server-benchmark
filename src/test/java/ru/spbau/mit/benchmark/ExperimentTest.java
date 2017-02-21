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
    public void testAllArchitecturesFinishes() throws IOException, BrokenBarrierException, InterruptedException {
        for (ServerArchitecture sa : ServerArchitecture.values()) {
            experimentOnce(sa, VaryingParameter.ELEMENTS_PER_REQ);
        }
    }

    @Test
    public void testAllParamsFinishes() throws InterruptedException, BrokenBarrierException, IOException {
        final BenchmarkParameters bp = new BenchmarkParameters(15, 5, 10, 100);
        final ServerArchitecture sa = ServerArchitecture.TCP_ASYNCHRONOUS;
        experiment(sa, bp, VaryingParameter.ELEMENTS_PER_REQ, 10, 100, 20);
        experiment(sa, bp, VaryingParameter.CLIENTS_PARALLEL, 1, 100, 20);
        experiment(sa, bp, VaryingParameter.TIME_DELTA, 200, 2000, 200);
    }

    private void experimentOnce(ServerArchitecture sa, VaryingParameter vp) throws InterruptedException, IOException, BrokenBarrierException {
        final BenchmarkParameters bp = new BenchmarkParameters(15, 5, 10, 100);
        experiment(sa, bp, vp, 10, 100, 20);
    }

    private void experiment(ServerArchitecture sa, BenchmarkParameters bp, VaryingParameter vp,
                            int from, int to, int step) throws InterruptedException, IOException, BrokenBarrierException {
        final RunnerClient rc = new RunnerClient(InetAddress.getLocalHost(), sa);
        rc.run();
        Thread.sleep(500);

        final BenchmarkRunner br = new BenchmarkRunner(new ClientServerFactory(sa), InetAddress.getLocalHost());
        final Experiment experiment = new Experiment(br, bp, rc, vp, from, to, step);

        experiment.run();
        rc.stop();
    }
}