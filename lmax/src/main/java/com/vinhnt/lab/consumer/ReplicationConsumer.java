package com.vinhnt.lab.consumer;

import com.lmax.disruptor.EventHandler;
import com.vinhnt.lab.event.EventObject;


public class ReplicationConsumer implements EventHandler<EventObject> {

    @Override
    public void onEvent(EventObject eventObject, long l, boolean b) throws Exception {
        try {
            Thread.sleep(200);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());
        }
        System.out.println("ReplicationConsumer handler " + eventObject.getCorrelationId());
    }
}
