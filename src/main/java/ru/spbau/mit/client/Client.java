package ru.spbau.mit.client;

//import ru.spbau.mit.server.MultiThreadedServer;

import ru.mit.spbau.FlyingDataProtos;
import ru.spbau.mit.Sorter;
import ru.spbau.mit.server.ThreadPooledServer;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Client {

//    private static final int portNumber = 8080;

    public static void main(String[] args) throws IOException {
        final ThreadPooledServer tps = new ThreadPooledServer();
        tps.start();
        final Socket socket = new Socket("localhost", tps.getCurrentPort());

        final DataInputStream in = new DataInputStream(socket.getInputStream());
        final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        int[] arrToSort = Sorter.generateArr(10);
        List<Integer> iterable = IntStream.of(arrToSort).boxed().collect(Collectors.toList());
        final FlyingDataProtos.FlyingData fd = FlyingDataProtos.FlyingData
                .newBuilder().addAllValue(iterable).build();

        out.writeByte(1);
        out.writeInt(fd.getSerializedSize());
        out.write(fd.toByteArray());

        final int serializedSize  = in.readInt();
        byte[] content = new byte[serializedSize];
        in.readFully(content);
        final FlyingDataProtos.FlyingData newFd = FlyingDataProtos.FlyingData.parseFrom(content);
        final List<Integer> values = fd.getValueList();
        int[] array = values.stream().mapToInt(x->x).toArray();

        out.close();
        in.close();
        tps.stop();

        Arrays.stream(array).forEach(x -> System.out.println());
    }
}

