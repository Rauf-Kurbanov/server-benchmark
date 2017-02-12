package ru.spbau.mit.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// TODO consider using builder
//@Data
public class ServerTimestamp {
    @Getter
    private final long requestProcessingTime;
    @Getter
    private final long clientProcessingTime;
}