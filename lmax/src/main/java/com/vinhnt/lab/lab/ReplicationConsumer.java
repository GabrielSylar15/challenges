package com.vinhnt.lab.lab;

import com.lmax.disruptor.EventHandler;
import com.vinhnt.lab.event.EventObject;

public class ReplicationConsumer implements EventHandler<EventObject> {
    @Override
    public void onEvent(EventObject valueEvent, final long sequence, final boolean endOfBatch) throws Exception {
        Thread.sleep(500);
        System.out.println("ReplicationConsumer with value : " + valueEvent.getCorrelationId() + " - Sequence : " + sequence);
    }
}