package com.vinhnt.lab.consumer;

import com.lmax.disruptor.ExceptionHandler;

/**
 * Any un-handled or thrown exception in processing by an event handler will
 * be reported through an implementation of ExceptionHandler. Depending upon
 * which step in our "diamond configuration" has failed, we would take
 * action. For example, if posting failed after journaling and replication, 
 * we might issue compensating journal and replication events.
 */
public class GenericExceptionHandler implements ExceptionHandler {

    public void handleEventException(Throwable ex, long sequence, Object event) {
    }

    public void handleOnStartException(Throwable ex) {
    }

    public void handleOnShutdownException(Throwable ex) {
    }

}