package com.vinhnt.lab.consumer;

import com.lmax.disruptor.EventHandler;
import com.vinhnt.lab.event.EventObject;

import java.util.concurrent.CompletableFuture;

public class CombineEventConsumer implements EventHandler<EventObject> {

    @Override
    public void onEvent(EventObject event, long sequence, boolean endOfBatch) {
        CompletableFuture<Void> journalStage = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("JournalConsumer processing: " + event.getCorrelationId() + " at sequence: " + sequence);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        CompletableFuture<Void> replicationStage = journalStage.thenRunAsync(() -> {
            try {
                System.out.println("ReplicationConsumer processing: " + event.getCorrelationId() + " at sequence: " + sequence);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        CompletableFuture<Void> businessLogicStage = replicationStage.thenRunAsync(() -> {
            try {
                System.out.println("BusinessLogicConsumer processing: " + event.getCorrelationId() + " at sequence: " + sequence);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Ensure completion
        businessLogicStage.join();
    }
}
