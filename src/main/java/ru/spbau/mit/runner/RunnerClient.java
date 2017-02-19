package ru.spbau.mit.runner;

import ru.mit.spbau.FlyingDataProtos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class RunnerClient {
    private Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public RunnerClient(InetAddress serverAddress) throws IOException {
        socket = new Socket(serverAddress, RunnerProtocol.SERVER_RUNNER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    // TODO rename
    public void run(ServerArchitecture sa) throws IOException {
        RunnerProtocol.sendConnectionRequest(out, sa);
        RunnerProtocol.recieveConfirmation(in);
    }

    public FlyingDataProtos.BenchmarkResult askStatistics() throws IOException {
//        RunnerProtocol.sendConfirmation(out);
        RunnerProtocol.sendBenchmarkRequest(out);
        return RunnerProtocol.reciveBenchmarkResult(in);
    }

    public void stop() throws IOException {
        socket.close();
        in.close();
        out.close();
    }
}
