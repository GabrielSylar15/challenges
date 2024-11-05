package com.vinhnt.lab.consumer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;
import com.vinhnt.lab.event.EventObject;

import java.util.Random;

public class JournalConsumer implements EventHandler<EventObject> {
    Sequence processedSequence;


    @Override
    public void setSequenceCallback(Sequence sequenceCallback) {
        processedSequence = sequenceCallback;
    }

    @Override
    public void onEvent(EventObject eventObject, long l, boolean b) throws Exception {
        try {
            Thread.sleep(20);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());

        }
        processedSequence.set(l);
        System.out.println("JournalConsumer handler " + eventObject.getCorrelationId() + " sequence: " + l);
    }

}
