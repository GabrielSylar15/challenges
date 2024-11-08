package com.vinhnt.lab.consumer;

public class WorkDistributor {
    private final int numConsumers;

    public WorkDistributor(int numConsumers) {
        this.numConsumers = numConsumers;
    }

    public int getConsumerId(long sequence) {
        return (int) (sequence % numConsumers);
    }
}