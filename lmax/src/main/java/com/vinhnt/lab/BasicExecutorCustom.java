package com.vinhnt.lab;

import com.lmax.disruptor.dsl.BasicExecutor;

import java.util.concurrent.ThreadFactory;

public class BasicExecutorCustom extends BasicExecutor {

    public BasicExecutorCustom(ThreadFactory factory) {
        super(factory);
    }

    @Override
    public String toString() {
        return "BasicExecutorCustom";
    }
}
