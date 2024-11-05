package com.vinhnt.lab.lab;

import com.lmax.disruptor.EventHandler;
import com.vinhnt.lab.event.EventObject;

import java.util.concurrent.CountDownLatch;

public class ApplicationConsumer implements EventHandler<EventObject> {
    private CountDownLatch latch;

    public void reset(final CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onEvent(EventObject valueEvent, final long sequence, final boolean endOfBatch) throws Exception {
        System.out.println("ApplicationConsumer with value : " + valueEvent.getCorrelationId() + " - Sequence : " + sequence);
        latch.countDown();
    }
}