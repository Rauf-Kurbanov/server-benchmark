package ru.spbau.mit.runner;

import lombok.Getter;
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
    @Getter
    private final ServerArchitecture serverArchitecture;

    public RunnerClient(InetAddress serverAddress, ServerArchitecture sa) throws IOException {
        this.serverArchitecture = sa;
        socket = new Socket(serverAddress, RunnerProtocol.SERVER_RUNNER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    // TODO future
    public void run() throws IOException {
        RunnerProtocol.sendConnectionRequest(out, serverArchitecture);
        RunnerProtocol.recieveConfirmation(in);
    }

    public FlyingDataProtos.BenchmarkResult askStatistics() throws IOException {
        RunnerProtocol.sendBenchmarkRequest(out);
        return RunnerProtocol.reciveBenchmarkResult(in);
    }

    public void stop() throws IOException {
        socket.close();
        in.close();
        out.close();
    }
}
