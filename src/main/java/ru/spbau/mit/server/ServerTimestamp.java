package ru.spbau.mit.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO eliminate; same thing as benchmark result
@RequiredArgsConstructor
// TODO consider using builder
public class ServerTimestamp {

//    public static ServerTimestamp fromNano(long nanoRequestTime, long nanoClientTime) {
//        return new ServerTimestamp(nanoRequestTime / 1_000_000
//                , nanoClientTime / 1_000_000);
//    }

    @Getter
    private final long requestProcessingTime;
    @Getter
    private final long clientProcessingTime;
}