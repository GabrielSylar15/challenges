package com.vinhnt.lab;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.vinhnt.lab.consumer.BusinessLogicConsumer;
import com.vinhnt.lab.consumer.JournalConsumer;
import com.vinhnt.lab.consumer.ReplicationConsumer;
import com.vinhnt.lab.event.EventObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

// https://batnamv.medium.com/lmax-disruptor-4621a8bb2b99
// https://martinfowler.com/articles/lmax.html
// https://voz.vn/t/cach-ap-dung-lmax-disruptor-voi-spring.776464/
// https://engineering.tiki.vn/arcturus-inventory-processing-system/?fbclid=IwY2xjawERm49leHRuA2FlbQIxMAABHcFoYkbrvngOisKn-mkF_mZsXaPufccppn3m92N2chuC406GNLI95j-MUg_aem_jkB-RKt9Wl4INMmceNtilQ
public class Main {
    private static final int NUM_EVENT_PROCESSORS = 3;
    private static final int BUFFER_SIZE = 32;


    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_EVENT_PROCESSORS, DaemonThreadFactory.INSTANCE);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        Disruptor<EventObject> disruptor = new Disruptor<>(
                EventObject.EVENT_FACTORY,
                BUFFER_SIZE,
                threadFactory,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        RingBuffer<EventObject> ringBuffer = disruptor.getRingBuffer();
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        JournalConsumer journalConsumer = new JournalConsumer();
        BatchEventProcessor<EventObject> batchEventProcessorJournal =
                new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrier, journalConsumer);

        ReplicationConsumer replicationConsumer = new ReplicationConsumer();
        BatchEventProcessor<EventObject> batchEventProcessorReplication =
                new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrier, replicationConsumer);

        SequenceBarrier sequenceBarrierConclusion =
                ringBuffer.newBarrier(batchEventProcessorJournal.getSequence(), batchEventProcessorReplication.getSequence());
        BusinessLogicConsumer applicationConsumer = new BusinessLogicConsumer();
        BatchEventProcessor<EventObject> batchEventProcessorApplication =
                new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrier, applicationConsumer);

        ringBuffer.addGatingSequences(batchEventProcessorApplication.getSequence());

        disruptor.handleEventsWith(journalConsumer)
                .then(replicationConsumer)
                .then(applicationConsumer);


        disruptor.start();
        long startTime = System.nanoTime();
        for (long i = 0; i < 150; i++) {
            long sequence = ringBuffer.next();
            try {
                EventObject event = ringBuffer.get(sequence);
                event.setCorrelationId(i);
            } finally {
                System.out.println("Publish event " + i);
                ringBuffer.publish(sequence);
            }
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInSeconds = duration / 1_000_000_000.0; // Chuyển đổi sang giây

        System.out.println("Processing time for 128 messages: " + durationInSeconds + "ms");
    }

}