package ru.spbau.mit.runner;

import ru.mit.spbau.FlyingDataProtos;

import java.io.*;

public class RunnerProtocol {

    public static final int SERVER_RUNNER_PORT = 5432;
    public static final int TESTED_SERVER_PORT = 6869;

    public static void sendConnectionRequest(DataOutputStream output, ServerArchitecture sa) throws IOException {
        output.writeInt(1);
        FlyingDataProtos.ServerType.newBuilder()
                .setServerType(sa.toString())
                .build()
                .writeDelimitedTo(output);
    }

    public static ServerArchitecture receiveConnectionRequest(InputStream input) throws IOException {
        FlyingDataProtos.ServerType type = FlyingDataProtos.ServerType.parseDelimitedFrom(input);
        if (type == null)
            throw new EOFException("Can't have null as serverType");
        return ServerArchitecture.valueOf(type.getServerType());
    }

    public static void sendBenchmarkResult(DataOutputStream out, FlyingDataProtos.BenchmarkResult br) throws IOException {
        br.writeDelimitedTo(out);
    }

    public static FlyingDataProtos.BenchmarkResult reciveBenchmarkResult(DataInputStream in) throws IOException {
        return FlyingDataProtos.BenchmarkResult.parseDelimitedFrom(in);
    }

    public static void sendBenchmarkRequest(DataOutputStream out) throws IOException {
        out.writeInt(2);
    }

    public static void sendConfirmation(DataOutputStream output) throws IOException {
        output.writeInt(3);
    }

    public static void recieveConfirmation(DataInputStream in) throws IOException {
        final int status = in.readInt();
        if (status != 3) {
            throw new IOException("Wrong confirmation status");
        }
    }
}
