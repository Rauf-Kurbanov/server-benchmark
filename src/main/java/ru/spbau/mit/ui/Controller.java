package ru.spbau.mit.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.spbau.mit.benchmark.BenchmarkParameters;
import ru.spbau.mit.benchmark.BenchmarkRunner;
import ru.spbau.mit.benchmark.ClientServerFactory;
import ru.spbau.mit.runner.Experiment;
import ru.spbau.mit.runner.RunnerClient;
import ru.spbau.mit.runner.ServerArchitecture;
import ru.spbau.mit.runner.VaryingParameter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BrokenBarrierException;

// TODO java FX initialization order
public class Controller extends Application {

    @FXML
    private Button startBtn;
    @FXML
    private ChoiceBox architectureChoice;
    @FXML
    private TextField reqsPerClient;
    @FXML
    private TextField arraySize;
    @FXML
    private TextField clientsSize;
    @FXML
    private TextField nextReqDelta;

    @FXML
    private ChoiceBox varyingChoice;
    @FXML
    private TextField varyingFrom;
    @FXML
    private TextField varyingTo;
    @FXML
    private TextField varyingStep;

    private static InetAddress serverAddress;

    public Controller() throws IOException {
    }

    public static void main(String[] args) throws UnknownHostException {
        if (args.length != 0) {
            serverAddress = InetAddress.getByName(args[0]);
        } else {
            serverAddress = InetAddress.getLocalHost();
        }
        launch(args);
    }

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/startwindow.fxml"));
            final VBox page = loader.load();
            final Controller controller = loader.getController();
//            controller.setup();

            final Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Network performance");
            primaryStage.show();
        } catch (IOException e) {
        }
    }


    @FXML
    private void initialize() {
        setup();
    }

    private void setup() {
        architectureChoice.setItems(FXCollections.observableArrayList(ServerArchitecture.values()));
        architectureChoice.getSelectionModel().selectFirst();

        varyingChoice.setItems(FXCollections.observableArrayList(VaryingParameter.values()));
        varyingChoice.getSelectionModel().selectFirst();

        arraySize.setText("15");
        clientsSize.setText("15");
        reqsPerClient.setText("20");
        nextReqDelta.setText("100");

        varyingFrom.setText("1000");
        varyingTo.setText("5000");
        varyingStep.setText("1000");

        setupUploadButton();
    }

    private BenchmarkParameters getBenchmarkParameters() {
        final int inputArrSize = Integer.valueOf(arraySize.getText());
        final int nClients = Integer.valueOf(clientsSize.getText());
        final int nQueries = Integer.valueOf(reqsPerClient.getText());
        final int delay = Integer.valueOf(nextReqDelta.getText());
        return new BenchmarkParameters(inputArrSize, nClients, nQueries, delay);
    }

    private RunnerClient runnerClient;

    private volatile LineChart<? extends Number, ? extends Number> paramClientChart;
    private volatile LineChart<? extends Number, ? extends Number> paramQueryChart;
    private volatile LineChart<? extends Number, ? extends Number> paramConnectionChart;

    private void setupUploadButton() {
        startBtn.setOnMousePressed(mouseEvent -> {
            try {
                final ServerArchitecture sa = (ServerArchitecture) architectureChoice.getValue();

                runnerClient = new RunnerClient(serverAddress, sa);
                runnerClient.run();

                final BenchmarkParameters bp = getBenchmarkParameters();
                final BenchmarkRunner br = new BenchmarkRunner(new ClientServerFactory(sa), serverAddress);

                final VaryingParameter vp = (VaryingParameter) varyingChoice.getValue();
                final int from = Integer.parseInt(varyingFrom.getText());
                final int to = Integer.parseInt(varyingTo.getText());
                final int step = Integer.parseInt(varyingStep.getText());
                final Experiment experiment = new Experiment(br, bp, runnerClient, vp, from, to, step);

                experiment.run();
                paramQueryChart = experiment.getParamQueryChart();
                paramConnectionChart = experiment.getParamConnectionChart();
                paramClientChart = experiment.getParamClientChart();

                runnerClient.stop();

            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        startBtn.setOnMouseReleased(mouseEvent -> {
            assert(paramClientChart != null);
            final HBox hbox = new HBox(paramQueryChart, paramConnectionChart);
            final VBox vbox = new VBox(hbox, paramClientChart);
            final Scene scene = new Scene(vbox);

            final Stage stage = new Stage();
            stage.setTitle("Line Chart Sample");
            stage.setScene(scene);
            stage.show();
        });
    }
}