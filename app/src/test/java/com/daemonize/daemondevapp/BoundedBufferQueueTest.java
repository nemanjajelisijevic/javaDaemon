package com.daemonize.daemondevapp;

import com.daemonize.daemonengine.consumer.BoundedBufferQueue;

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
}
