package com.vinhnt.lab.lab;

import com.lmax.disruptor.EventHandler;
import com.vinhnt.lab.event.EventObject;

public class JournalConsumer implements EventHandler<EventObject> {
    @Override
    public void onEvent(EventObject valueEvent, final long sequence, final boolean endOfBatch) throws Exception {
        System.out.println("JournalConsumer with value : " + valueEvent.getCorrelationId() + " - Sequence : " + sequence);
    }
}