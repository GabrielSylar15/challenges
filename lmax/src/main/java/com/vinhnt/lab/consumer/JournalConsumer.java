package com.vinhnt.lab.consumer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;
import com.vinhnt.lab.event.EventObject;

import java.util.Random;

public class JournalConsumer implements EventHandler<EventObject> {
    private final int consumerId;
    private final WorkDistributor workDistributor;

    public JournalConsumer(int consumerId, WorkDistributor workDistributor) {
        this.consumerId = consumerId;
        this.workDistributor = workDistributor;
    }

    @Override
    public void onEvent(EventObject event, long sequence, boolean endOfBatch) {
        // Chỉ xử lý các sự kiện được phân phối cho consumer này
        if (workDistributor.getConsumerId(sequence) == consumerId) {
            processEvent(event, sequence);
        }
    }

    private void processEvent(EventObject eventObject, long sequence) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("JournalConsumer " + consumerId + " processed event " +
                           eventObject.getCorrelationId() + " with sequence: " + sequence);
    }

    private boolean isLogicalChunkOfWorkComplete()
    {
        // Ret true or false based on whatever criteria is required for the smaller
        // chunk.  If this is doing I/O, it may be after flushing/syncing to disk
        // or at the end of DB batch+commit.
        // Or it could simply be working off a smaller batch size.

//        return --batchRemaining == -1;
        return true;
    }

    private void processEvent(final EventObject eventObject, Long l)
    {
        try {
            Thread.sleep(20);
        } catch (Exception ex) {
            System.out.println("Thread handler error" + ex.getMessage());

        }
        System.out.println("JournalConsumer handler " + eventObject.getCorrelationId() + " sequence: " + l);
    }

}
