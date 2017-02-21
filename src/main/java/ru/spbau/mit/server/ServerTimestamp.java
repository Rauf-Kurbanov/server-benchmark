package ru.spbau.mit.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerTimestamp {
    @Getter
    private final long requestProcessingTime;
    @Getter
    private final long clientProcessingTime;
}