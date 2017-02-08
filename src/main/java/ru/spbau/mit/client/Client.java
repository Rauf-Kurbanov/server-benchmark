package ru.spbau.mit.client;

//import ru.spbau.mit.server.MultiThreadedServer;

import lombok.RequiredArgsConstructor;
import ru.spbau.mit.Sorter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

@RequiredArgsConstructor
public class Client implements Runnable {

    private final int n;
    private final int portNumber;
    private final Socket socket;

    private final DataInputStream in;
    private final DataOutputStream out;

    // TODO localhost -> host
    public Client(int portNumber, int n) throws IOException {
        this.n = n;
        this.portNumber = portNumber;
        socket = new Socket("localhost", portNumber);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public List<Integer> askToSort() throws IOException {
        final int[] arrToSort = Sorter.generateArr(n);
        Protocol.sendSortRequest(out, arrToSort);
        final List<Integer> res = Protocol.getSortResponse(in);
        return res;
    }

    public void stop() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
    }

    @Override
    public void run() {

    }
}

