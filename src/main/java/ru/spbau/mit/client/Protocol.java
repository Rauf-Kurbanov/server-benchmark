package ru.spbau.mit.client;

import ru.mit.spbau.FlyingDataProtos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Protocol {

    int MAX_MESSAGE_SIZE = 10 * 1024;

    static void sendSortRequest(DataOutputStream out, int[] arrToSort) throws IOException {
        final List<Integer> iterable = IntStream.of(arrToSort).boxed().collect(Collectors.toList());
        final FlyingDataProtos.FlyingData fd = FlyingDataProtos.FlyingData
                .newBuilder().addAllValue(iterable).build();

        out.writeInt(fd.getSerializedSize());
        out.write(fd.toByteArray());
    }

    static List<Integer> getSortResponse(DataInputStream in) throws IOException {
        final int serializedSize = in.readInt();
        System.out.println("serializedSize = " + serializedSize);
        final byte[] content = new byte[serializedSize];
        in.readFully(content);
        final FlyingDataProtos.FlyingData newFd = FlyingDataProtos.FlyingData.parseFrom(content);
        return newFd.getValueList();
    }


}
