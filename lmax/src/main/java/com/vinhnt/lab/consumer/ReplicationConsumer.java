package com.vinhnt.lab.consumer;

import com.lmax.disruptor.WorkHandler;
import com.vinhnt.lab.event.EventObject;


public class ReplicationConsumer implements WorkHandler<EventObject> {

    @Override
    public void onEvent(EventObject eventObject) throws Exception {
        try {
            Thread.sleep(200);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());
        }
//        System.out.println("ReplicationConsumer handler " + eventObject.getCorrelationId());
    }
}
