package com.vinhnt.lab.consumer;

import com.lmax.disruptor.EventHandler;
import com.vinhnt.lab.event.EventObject;

import java.util.Random;

public class JournalConsumer implements EventHandler<EventObject> {

    @Override
    public void onEvent(EventObject eventObject, long l, boolean b) throws Exception {
        try {
            Random random = new Random();
            int randomNumber = 1000 + random.nextInt(201);
            Thread.sleep(randomNumber);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());

        }
        System.out.println("JournalConsumer handler " + eventObject.getCorrelationId() + " sequence: " + l);
    }

}
