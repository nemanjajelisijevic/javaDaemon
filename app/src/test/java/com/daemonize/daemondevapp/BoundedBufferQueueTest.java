package com.daemonize.daemondevapp;

import com.daemonize.daemonengine.utils.BoundedBufferQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BoundedBufferQueueTest {

    private int expectedsSize = 800;
    private int queueCapacity = 79;
    private List<Integer> expecteds = new ArrayList<>(expectedsSize);
    private BoundedBufferQueue<Integer> queue;

    @Before
    public void setUp() {

        for (int i = 0; i < expectedsSize; ++i) {
            expecteds.add(i * 7);
        }

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
            System.out.println("Queue state:" + queue.toString());

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
    public void test() {

        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        System.out.println("*******************************************************");

        queue.add(0);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        Object[] array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(1, queue.size());

        System.out.println("*******************************************************");

        queue.add(1);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(2, queue.size());
        Assert.assertArrayEquals(new Integer[]{0, 1}, queue.toArray());

        System.out.println("*******************************************************");

        queue.add(2);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(3, queue.size());

        System.out.println("*******************************************************");

        queue.add(3);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(4, queue.size());

        System.out.println("*******************************************************");

        queue.add(4);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(5, queue.size());

        System.out.println("*******************************************************");

        queue.add(5);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(6, queue.size());

        System.out.println("*******************************************************");

        queue.add(6);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(7, queue.size());

        System.out.println("*******************************************************");

        queue.add(7);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(8, queue.size());

        System.out.println("*******************************************************");

        queue.add(8);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(9, queue.size());

        System.out.println("*******************************************************");

        queue.add(9);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(10, queue.size());

        System.out.println("*******************************************************");

        queue.add(10);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(11, queue.size());

        System.out.println("*******************************************************");

        queue.add(11);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(12, queue.size());

        System.out.println("*******************************************************");

        queue.add(12);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(13, queue.size());

        System.out.println("*******************************************************");

        queue.add(13);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(14, queue.size());

        System.out.println("*******************************************************");

        queue.add(14);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(15, queue.size());

        System.out.println("*******************************************************");

        queue.add(15);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(16, queue.size());

        System.out.println("*******************************************************");

        queue.add(16);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(17, queue.size());

        System.out.println("*******************************************************");

        queue.add(17);
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(   18, queue.size());

        System.out.println("*******************************************************");

        queue.add(18);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(19, queue.size());

        System.out.println("*******************************************************");

        queue.add(19);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(20, queue.size());

        System.out.println("*******************************************************");


        Integer removed = queue.poll();
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(19, queue.size());

        System.out.println("*******************************************************");

        queue.add(20);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(20, queue.size());

        System.out.println("*******************************************************");

        try {

            queue.add(21);

        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("Queue full"));
        }


        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(20, queue.size());

        System.out.println("*******************************************************");

        removed = queue.poll();
        System.out.println("Removed: " + removed);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());

        System.out.println(queue.toString());

        Assert.assertEquals(19, queue.size());

        System.out.println("*******************************************************");

        removed = queue.poll();
        System.out.println("Size: " + queue.size());
        System.out.println("Removed: " + removed);
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());

        System.out.println(queue.toString());

        Assert.assertEquals(18, queue.size());

        System.out.println("*******************************************************");

        queue.add(22);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(19, queue.size());

        System.out.println("*******************************************************");

        queue.add(23);
        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(20, queue.size());


        System.out.println("*******************************************************");

        System.out.println("Trying to insert new element (24)");

        try {
            queue.add(24);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        System.out.println("Size: " + queue.size());
        System.out.println("Head: " + queue.getHead());
        System.out.println("Tail: " + queue.getTail());
        array = queue.toArray();
        for(int i = 0; i < array.length; ++i)
            System.out.println("Queue Element[" + i + "]: " + array[i]);

        System.out.println(queue.toString());

        Assert.assertEquals(20, queue.size());


        System.out.println("*******************************************************");
        System.out.println("Polling all elements");
        System.out.println("Size: " + queue.size());

        int expectedSize = 20;

        Assert.assertEquals(expectedSize, queue.size());

        while (!queue.isEmpty()) {
            System.out.println("**************");
            removed = queue.poll();
            System.out.println("Removed: " + removed);
            System.out.println("Size: " + queue.size());
            Assert.assertEquals(--expectedSize, queue.size());
            System.out.println(queue.toString());
            System.out.println("**************");
        }

        System.out.println("*******************************************************");

        System.out.println("Trying to remove one more past empty");

        removed = queue.poll();

        System.out.println("Removed: " + removed);

        Assert.assertNull(removed);
        Assert.assertEquals(0, queue.size());

    }

}
