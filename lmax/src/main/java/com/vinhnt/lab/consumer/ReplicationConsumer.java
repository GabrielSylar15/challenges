package com.vinhnt.lab.consumer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;
import com.vinhnt.lab.event.EventObject;


public class ReplicationConsumer implements EventHandler<EventObject> {
    private Sequence sequenceCallback;
    private int batchRemaining = 20;

    @Override
    public void setSequenceCallback(final Sequence sequenceCallback) {
        this.sequenceCallback = sequenceCallback;
    }

    @Override
    public void onEvent(final EventObject event, final long sequence, final boolean endOfBatch) {
        processEvent(event, sequence);
        sequenceCallback.set(sequence);

//        boolean logicalChunkOfWorkComplete = isLogicalChunkOfWorkComplete();
//        if (logicalChunkOfWorkComplete) {
//            sequenceCallback.set(sequence);
//        }
//
//        batchRemaining = logicalChunkOfWorkComplete || endOfBatch ? 20 : batchRemaining;
    }

    private boolean isLogicalChunkOfWorkComplete() {
        // Ret true or false based on whatever criteria is required for the smaller
        // chunk.  If this is doing I/O, it may be after flushing/syncing to disk
        // or at the end of DB batch+commit.
        // Or it could simply be working off a smaller batch size.

        return --batchRemaining == -1;
    }

    private void processEvent(final EventObject eventObject, Long l) {
        try {
            Thread.sleep(20);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());
        }
        System.out.println("ReplicationConsumer handler " + eventObject.getCorrelationId() + " sequence: " + l);
    }
}
