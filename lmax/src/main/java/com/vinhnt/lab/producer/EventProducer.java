package com.vinhnt.lab.producer;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.vinhnt.lab.event.EventObject;

public class EventProducer {
    private static final int BUFFER_SIZE = 1024 * 8;
//    private final RingBuffer<EventObject> ringBuffer =
//            new Disruptor<EventObject>(EventObject.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
}
