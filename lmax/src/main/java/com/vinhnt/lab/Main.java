package com.vinhnt.lab;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.vinhnt.lab.consumer.BusinessLogicConsumer;
import com.vinhnt.lab.consumer.JournalConsumer;
import com.vinhnt.lab.consumer.ReplicationConsumer;
import com.vinhnt.lab.event.EventObject;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

// https://batnamv.medium.com/lmax-disruptor-4621a8bb2b99
// https://martinfowler.com/articles/lmax.html
// https://voz.vn/t/cach-ap-dung-lmax-disruptor-voi-spring.776464/
// https://engineering.tiki.vn/arcturus-inventory-processing-system/?fbclid=IwY2xjawERm49leHRuA2FlbQIxMAABHcFoYkbrvngOisKn-mkF_mZsXaPufccppn3m92N2chuC406GNLI95j-MUg_aem_jkB-RKt9Wl4INMmceNtilQ
public class Main {
    private static final int NUM_EVENT_PROCESSORS = 3;
    private static final int BUFFER_SIZE = 1024 * 1024;


    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(NUM_EVENT_PROCESSORS, DaemonThreadFactory.INSTANCE);
//        ThreadFactory factory = Executors.defaultThreadFactory();
        final ThreadFactory factory = Thread.ofVirtual().factory();

        Disruptor<EventObject> disruptor = new Disruptor<>(
                EventObject.EVENT_FACTORY,
                BUFFER_SIZE,
                factory,
                ProducerType.SINGLE,
                new SleepingWaitStrategy()
        );

        RingBuffer<EventObject> ringBuffer = disruptor.getRingBuffer();
//        JournalConsumer journalConsumer = new JournalConsumer();
//        ReplicationConsumer replicationConsumer = new ReplicationConsumer();
//        BusinessLogicConsumer applicationConsumer = new BusinessLogicConsumer();

        WorkHandler<EventObject> journalConsumer = new JournalConsumer();
        WorkHandler<EventObject> replicationConsumer = new ReplicationConsumer();
        WorkHandler<EventObject> applicationConsumer = new BusinessLogicConsumer();

        WorkHandler<EventObject>[] journalConsumers = new WorkHandler[800];
        Arrays.fill(journalConsumers, journalConsumer);
        WorkHandler<EventObject>[] replicationConsumers = new WorkHandler[800];
        Arrays.fill(replicationConsumers, replicationConsumer);
        WorkHandler<EventObject>[] applicationConsumers = new WorkHandler[800];
        Arrays.fill(applicationConsumers, applicationConsumer);


        disruptor.handleEventsWithWorkerPool(journalConsumers)
                .handleEventsWithWorkerPool(replicationConsumers)
                .handleEventsWithWorkerPool(applicationConsumers);


        disruptor.start();
        long startTime = System.currentTimeMillis();
        for (long i = 0; i < 10_000; i++) {
            long sequence = ringBuffer.next();
            try {
                EventObject event = ringBuffer.get(sequence);
                event.setCorrelationId(i);
            } finally {
                System.out.println("Publish event " + i);
                ringBuffer.publish(sequence);
            }
        }
        disruptor.shutdown();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Processing time for messages: " + duration + "ms");
    }

}