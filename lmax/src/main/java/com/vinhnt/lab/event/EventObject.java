package com.vinhnt.lab.event;


import com.lmax.disruptor.EventFactory;

public class EventObject {
    private Long correlationId;

    public Long getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(Long correlationId) {
        this.correlationId = correlationId;
    }

    public static final EventFactory<EventObject> EVENT_FACTORY = new EventFactory<EventObject>() {
        @Override
        public EventObject newInstance() {
            return new EventObject();
        }
    };
}
