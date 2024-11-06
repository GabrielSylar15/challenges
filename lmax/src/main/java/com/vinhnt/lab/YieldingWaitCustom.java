package com.vinhnt.lab;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

public class YieldingWaitCustom implements WaitStrategy
{
    private static final int SPIN_TRIES = 100;

    @Override
    public long waitFor(
            final long sequence, Sequence cursor, final Sequence dependentSequence, final SequenceBarrier barrier)
            throws AlertException, InterruptedException
    {
        long availableSequence;
        int counter = SPIN_TRIES;
        while ((availableSequence = dependentSequence.get()) < sequence)
        {
            counter = applyWaitMethod(barrier, counter);
        }

        return availableSequence;
    }

    @Override
    public void signalAllWhenBlocking()
    {
    }

    private int applyWaitMethod(final SequenceBarrier barrier, int counter)
            throws AlertException
    {
        barrier.checkAlert();

        if (counter > 10)
        {
            --counter;
        }
        else if (counter == 0)
        {
            --counter;
            Thread.yield();
        } else {
            LockSupport.parkNanos(1);
        }

        return counter;
    }
}
