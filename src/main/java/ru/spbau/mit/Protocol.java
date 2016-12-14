package ru.spbau.mit;

import ru.mit.spbau.FlyingDataProtos;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Protocol {

    public void sendSortRequest(DataInputStream in, DataOutputStream out, int n, int[] arr) throws IOException {
        out.writeInt(n);
        for (int i = 0; i < n; i++) {
            out.write(arr[i]);
        }
    }

    public int[] getSortResponce(DataInputStream in, DataOutputStream out) throws IOException {
        final int n = in.readInt();
        final int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            res[i] = in.readInt();
        }
        return res;
    }


}
