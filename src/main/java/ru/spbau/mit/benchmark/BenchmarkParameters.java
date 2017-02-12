package ru.spbau.mit.benchmark;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BenchmarkParameters {
    @Getter
    private final int arraySize;
    @Getter
    private final int nClients;
    @Getter
    private final int nQueries;
    @Getter
    private final int delayInMs;

}

