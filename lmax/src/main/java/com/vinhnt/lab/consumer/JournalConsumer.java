package com.vinhnt.lab.consumer;

import com.lmax.disruptor.WorkHandler;
import com.vinhnt.lab.event.EventObject;

public class JournalConsumer implements WorkHandler<EventObject> {

    @Override
    public void onEvent(EventObject eventObject) throws Exception {
        try {
            Thread.sleep(200);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());

        }
//        System.out.println("JournalConsumer handler " + eventObject.getCorrelationId());
    }

}
