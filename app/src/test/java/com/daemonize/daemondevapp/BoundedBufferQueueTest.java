package com.daemonize.daemondevapp;

import com.daemonize.daemonengine.consumer.BoundedBufferQueue;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.TimeUnits;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BoundedBufferQueueTest {

    private int expectedsSize = 1000;
    private int queueCapacity = 91;
    private List<Integer> expecteds = new ArrayList<>(expectedsSize);
    private BoundedBufferQueue<Integer> queue;

    @Before
    public void setUp() {
        for (int i = 0; i < expectedsSize; ++i)
            expecteds.add(i * 7);
        queue = new BoundedBufferQueue<>(queueCapacity);
    }

    @Test
    public void testEndure() {

        int expectedCnt = 0;
        List<Integer> polls = new ArrayList<>(expectedsSize);

        for (int i = 0; i < expecteds.size(); ++i) {

            System.out.println("=========================================================");

            System.out.println("Adding expected: " + expecteds.get(i));

            try {
                queue.add(expecteds.get(i));
            } catch (IllegalStateException ex) {
                System.out.println("************************************************");
                System.out.println("Exception: " + ex.getMessage());
                Integer polled = queue.poll();
                System.out.println("Polled: " + polled);
                polls.add(polled);
                queue.add(expecteds.get(i));
                Assert.assertEquals(Integer.valueOf(expecteds.get(expectedCnt++)), polled);
                Assert.assertEquals(queueCapacity, queue.size());
                System.out.println("************************************************");
            }

            System.out.println("Queue size:" + queue.size());
            //System.out.println("Queue state:" + queue.toString());

            System.out.println("=========================================================");
        }

        System.out.println("DEPLETING QUEUE");

        Integer pollingExpected = expecteds.size() - queueCapacity;

        while (!queue.isEmpty()) {
            Integer polled = queue.poll();
            polls.add(polled);
            Assert.assertEquals(expecteds.get(pollingExpected++), polled);
            //System.out.println("Queue state: " + queue.toString());
        }

        Assert.assertNull(queue.poll());
        Assert.assertEquals(polls.size(), expectedsSize);

        for (int k = 0; k < expecteds.size(); ++k) {
            Assert.assertEquals(expecteds.get(k), polls.get(k));
        }

        System.out.println("Expecteds:\n");
        System.out.println(expecteds.toString());
        System.out.println("Polls:\n");
        System.out.println(polls.toString());

        queue.clear();
        Assert.assertEquals(0, queue.size());

        System.out.println("=========================================================");
    }

    @Test
    public void bench() {

        System.out.println("No of elements: " + expectedsSize);

        int noOfReps = 100;

        System.out.println("No of repetitions: " + noOfReps);

        long tFills = 0;
        long tDeps = 0;
        long tTots = 0;

        for (int t = 0; t < noOfReps; ++t) {

            long t0 = System.nanoTime();

            int expectedCnt = 0;
            List<Integer> polls = new ArrayList<>(expectedsSize);

            for (int i = 0; i < expecteds.size(); ++i) {

                if (i >= queueCapacity) {
                    Integer polled = queue.poll();
                    polls.add(polled);
                }

                queue.add(expecteds.get(i));
            }

            long t1 = System.nanoTime();

            while (!queue.isEmpty()) {
                Integer polled = queue.poll();
                polls.add(polled);
            }

            queue.clear();

            long t2 = System.nanoTime();

            tFills += (t1 - t0);
            tDeps += (t2 - t1);
            tTots += (t2 - t0);

        }

        System.out.println("Filling avg time: " + DaemonUtils.convertNanoTimeUnits( tFills / noOfReps, TimeUnits.MILLISECONDS));
        System.out.println("Depleting avg time: " + DaemonUtils.convertNanoTimeUnits( tDeps/ noOfReps, TimeUnits.MILLISECONDS));
        System.out.println("Totals avg time: " + DaemonUtils.convertNanoTimeUnits( tTots / noOfReps, TimeUnits.MILLISECONDS));

    }
}
