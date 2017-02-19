package ru.spbau.mit.benchmark;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenchmarkParameters {
    private int arraySize;
    private int nClients;
    private int nQueries;
    private int delayInMs;
}

