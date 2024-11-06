//package com.vinhnt.lab.lab;
//
//import com.lmax.disruptor.*;
//import com.lmax.disruptor.util.DaemonThreadFactory;
//import com.vinhnt.lab.event.EventObject;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static com.lmax.disruptor.RingBuffer.createSingleProducer;
//
//public class OneToThreeDiamondSequencedTest {
//    private static final int NUM_EVENT_PROCESSORS = 3;
//    private static final int BUFFER_SIZE = 8 * 1024;
//
//    private static final ExecutorService executor = Executors.newFixedThreadPool(NUM_EVENT_PROCESSORS, DaemonThreadFactory.INSTANCE);
//
//    private static final RingBuffer<EventObject> ringBuffer =
//            createSingleProducer(EventObject.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
//
//    private static final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
//    private static final com.vinhnt.lab.lab.JournalConsumer journalConsumer = new com.vinhnt.lab.lab.JournalConsumer();
//    private static final BatchEventProcessor<EventObject> batchEventProcessorJournal = new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrier, journalConsumer);
//    private static final com.vinhnt.lab.lab.ReplicationConsumer replicationConsumer = new com.vinhnt.lab.lab.ReplicationConsumer();
//    private static final BatchEventProcessor<EventObject> batchEventProcessorReplication = new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrier, replicationConsumer);
//    private static final SequenceBarrier sequenceBarrierConclusion =
//            ringBuffer.newBarrier(batchEventProcessorJournal.getSequence(), batchEventProcessorReplication.getSequence());
//    private static final com.vinhnt.lab.lab.ApplicationConsumer applicationConsumer = new com.vinhnt.lab.lab.ApplicationConsumer();
//    private static final BatchEventProcessor<EventObject> batchEventProcessorApplication = new BatchEventProcessorBuilder().build(ringBuffer, sequenceBarrierConclusion, applicationConsumer);
//    {
//        ringBuffer.addGatingSequences(batchEventProcessorApplication.getSequence());
//    }
//
//    public static void main(String[] args) throws InterruptedException, AlertException, TimeoutException {
//        int size = 20;
//        CountDownLatch countDownLatch = new CountDownLatch(size);
//        applicationConsumer.reset(countDownLatch);
//
//        executor.submit(batchEventProcessorJournal);
//        executor.submit(batchEventProcessorReplication);
//        executor.submit(batchEventProcessorApplication);
//
//        for (long i = 0; i < size; i++) {
//            long sequence = ringBuffer.next();
//            ringBuffer.get(sequence).setCorrelationId(i);
//            ringBuffer.publish(sequence);
//        }
//        countDownLatch.await();
//
//        // Signal that this EventProcessor should stop when it has finished consuming at the next clean break.
//        batchEventProcessorJournal.halt();
//        batchEventProcessorReplication.halt();
//        batchEventProcessorApplication.halt();
//        assert true;
//    }
//}