package ru.spbau.mit.runner;

import lombok.extern.slf4j.Slf4j;
import ru.mit.spbau.FlyingDataProtos;
import ru.spbau.mit.benchmark.ClientServerFactory;
import ru.spbau.mit.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ServerRunner {
    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private ServerSocket serverSocket;

    private Server testedServer;

    public void start() {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(RunnerProtocol.SERVER_RUNNER_PORT);
            } catch (IOException ignored) {
            }
        });
    }

    private void runServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();
                try {
                    while (!clientSocket.isClosed()) {
                        answerStartServerQuery(clientSocket);
                    }
                } catch (IOException ignored) {}
                log.info("shutting down server");
                testedServer.shutdown();
            } catch (IOException e) {
                System.err.println("Can't answer server request");
                System.err.println(e.getMessage());
            }
        }
    }

    private void answerStartServerQuery(Socket clientSocket) throws IOException {
        final DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        final DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        final int requestType = in.readInt();
        switch (requestType) {
            case 1:
                log.info("handling connect");
                final ServerArchitecture sa = RunnerProtocol.receiveConnectionRequest(in);
                log.info("strating " + sa);
                testedServer = new ClientServerFactory(sa).getServer();
                testedServer.start(RunnerProtocol.TESTED_SERVER_PORT);
                RunnerProtocol.sendConfirmation(out);
                break;
            case 2:
                final FlyingDataProtos.BenchmarkResult bres = testedServer.getBenchmarkResult();
                RunnerProtocol.sendBenchmarkResult(out, bres);
                break;
            default:
                throw new IOException("unknown command");
        }
    }

    public void stop() throws IOException {
        testedServer.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
        serverThreadExecutor.shutdownNow();
    }

    public static void main(String[] args) {
        new ServerRunner().start();
    }
}
