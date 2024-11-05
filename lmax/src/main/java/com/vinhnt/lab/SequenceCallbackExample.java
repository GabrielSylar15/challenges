package com.vinhnt.lab;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.vinhnt.lab.event.EventObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SequenceCallbackExample {


    static class MessageEventFactory implements EventFactory<EventObject> {
        @Override
        public EventObject newInstance() {
            return new EventObject();
        }
    }

    static class JournalConsumer implements EventHandler<EventObject> {
        private final Sequence sequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);

        @Override
        public void onEvent(EventObject event, long sequence, boolean endOfBatch) throws InterruptedException {
            System.out.println("JournalConsumer processing: " + event.getCorrelationId() + " at sequence: " + sequence);
            Thread.sleep(500); // Giả lập xử lý I/O
            this.sequence.set(sequence); // Cập nhật sequence sau khi hoàn thành
        }

        @Override
        public void onStart() {}

        @Override
        public void onShutdown() {}

        public Sequence getSequence() {
            return sequence;
        }
    }

    static class ReplicationConsumer implements EventHandler<EventObject> {
        private final Sequence sequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);

        @Override
        public void onEvent(EventObject event, long sequence, boolean endOfBatch) throws InterruptedException {
            System.out.println("ReplicationConsumer processing: " + event.getCorrelationId() + " at sequence: " + sequence);
            Thread.sleep(300); // Giả lập xử lý I/O
            this.sequence.set(sequence); // Cập nhật sequence sau khi hoàn thành
        }

        public Sequence getSequence() {
            return sequence;
        }
    }

    static class BusinessLogicConsumer implements EventHandler<EventObject> {
        @Override
        public void onEvent(EventObject event, long sequence, boolean endOfBatch) throws InterruptedException {
            System.out.println("BusinessLogicConsumer processing: " + event.getCorrelationId() + " at sequence: " + sequence);
            Thread.sleep(200); // Giả lập xử lý logic nghiệp vụ
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        MessageEventFactory factory = new MessageEventFactory();
        int bufferSize = 1024;
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        Disruptor<EventObject> disruptor = new Disruptor<>(
                EventObject.EVENT_FACTORY,
                bufferSize,
                threadFactory,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        JournalConsumer journalConsumer = new JournalConsumer();
        ReplicationConsumer replicationConsumer = new ReplicationConsumer();
        BusinessLogicConsumer businessLogicConsumer = new BusinessLogicConsumer();

        disruptor.handleEventsWith(journalConsumer)
                .then(replicationConsumer)
                .then(businessLogicConsumer);

        RingBuffer<EventObject> ringBuffer = disruptor.start();

        for (long i = 0; i < 1000; i++) {
            long sequence = ringBuffer.next();
            try {
                EventObject event = ringBuffer.get(sequence);
                event.setCorrelationId(i);
            } finally {
                ringBuffer.publish(sequence);
            }
            Thread.sleep(100); // Giả lập khoảng cách giữa các message
        }

        Thread.sleep(8000); // Chờ cho tất cả các sự kiện được xử lý
        disruptor.shutdown();
        executor.shutdown();
    }
}
