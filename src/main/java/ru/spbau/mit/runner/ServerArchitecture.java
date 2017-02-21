package ru.spbau.mit.runner;

public enum ServerArchitecture {
    TCP_ASYNCHRONOUS,
    TCP_NON_BLOCKING,
    TCP_SINGLE_THREAD,
    TCP_THREAD_POOLED,
    UDP_SINGLE_THREAD,
    UDP_THREADPOOLED
}