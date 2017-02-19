package ru.spbau.mit.runner;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import lombok.RequiredArgsConstructor;
import ru.mit.spbau.FlyingDataProtos;
import ru.spbau.mit.benchmark.BenchmarkParameters;
import ru.spbau.mit.benchmark.BenchmarkRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Experiment {
    private final BenchmarkRunner benchmarkRunner;
    private final BenchmarkParameters initalBP;
    private final RunnerClient runnerClient;
    private final VaryingParameter varyingParameter;
    private final List<Integer> varyingRange;

    private final List<Long> timesPerClient = new ArrayList<>();
    private final List<Long> timesPerQuery = new ArrayList<>();
    private final List<Long> timesPerConnection = new ArrayList<>();

    public Experiment(BenchmarkRunner br, BenchmarkParameters bp, RunnerClient rc,
                      VaryingParameter vp, int varyingFrom, int varyingTo, int varyingStep) {
        this.benchmarkRunner = br;
        this.initalBP = bp;
        this.runnerClient = rc;
        this.varyingParameter = vp;
        this.varyingRange = IntStream
                .rangeClosed(varyingFrom, varyingTo).filter(x -> x % varyingStep == 0)
                .boxed().collect(Collectors.toList());
        System.out.println("Experiment constructed");
    }

    public void run() throws BrokenBarrierException, InterruptedException, IOException {
        for (int param : varyingRange) {
            switch (varyingParameter) {
                case ELEMENTS_PER_REQ:
                    initalBP.setArraySize(param);
                    break;
                case CLIENTS_PARALLEL:
                    initalBP.setNClients(param);
                    break;
                case TIME_DELTA:
                    initalBP.setDelayInMs(param);
                    break;
            }
            final long averageTimePerClient = benchmarkRunner.start(initalBP);
            timesPerClient.add(averageTimePerClient);
            final FlyingDataProtos.BenchmarkResult bres = runnerClient.askStatistics();
            System.out.println("BRES");
            System.out.println(bres);
            timesPerQuery.add(bres.getQueryProcessingTime());
            timesPerConnection.add(bres.getClientProcessingTime());
        }
    }

    public LineChart<? extends Number, ? extends Number> getParamClientChart() {
        return makeChart(varyingRange, timesPerClient, "Client running time");
    }

    public LineChart<? extends Number, ? extends Number> getParamQueryChart() {
        // TODO
        for  (long l : timesPerQuery) {
            System.out.print(l + ", ");
        }
        System.out.println();

        return makeChart(varyingRange, timesPerQuery, "Query processing time");
    }

    public LineChart<? extends Number, ? extends Number> getParamConnectionChart() {
        for  (long l : timesPerConnection) {
            System.out.print(l + ", ");
        }
        System.out.println();

        return makeChart(varyingRange, timesPerConnection, "Client processing time");
    }

    private LineChart<Number, Number> makeChart(List<? extends Number> xs, List<? extends Number> ys,
                                                String title) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        final String xLabel = varyingParameter.toString();
        xAxis.setLabel(xLabel);
        yAxis.setLabel("Time in ms");
        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        XYChart.Series series = new XYChart.Series();
        series.setName("Time series");
        final Iterator<? extends Number> xi = xs.iterator();
        final Iterator<? extends Number> yi = ys.iterator();
        while (xi.hasNext() && yi.hasNext()) {
            series.getData().add(new XYChart.Data(xi.next(), yi.next()));
        }
        lineChart.getData().add(series);
        return lineChart;
    }
}
