package ru.spbau.mit.client;

import ru.spbau.mit.Sorter;
import ru.spbau.mit.benchmark.BenchmarkParameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class SingleTcpClient extends Client {
    private final InetAddress serverAddress;
    private final int portNumber;
    private final int arraySize;

    public SingleTcpClient(InetAddress serverAddress, int portNumber
            , int arraySize, int delayInMs, int nQueries) throws IOException {
        this.serverAddress = serverAddress;
        this.portNumber = portNumber;
        this.arraySize = arraySize;
        this.delayInMs = delayInMs;
        this.nQueries = nQueries;
    }

    public SingleTcpClient(InetAddress serverAddress, int portNumber, BenchmarkParameters bp) throws IOException {
        this(serverAddress, portNumber, bp.getArraySize(), bp.getDelayInMs(), bp.getNQueries());
    }

    // TODO why sending array and receiving list?
    @Override
    public List<Integer> askToSort() throws IOException {
        final Socket socket = new Socket(serverAddress, portNumber);
        final DataInputStream in = new DataInputStream(socket.getInputStream());
        final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        final int[] arrToSort = Sorter.generateArr(arraySize);
        Protocol.sendSortRequest(out, arrToSort);
        final List<Integer> res = Protocol.getSortResponse(in);
        socket.close();
        return res;
    }

    @Override
    public void stop() throws IOException {
    }
}
