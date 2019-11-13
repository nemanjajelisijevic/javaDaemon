package com.daemonize.daemondevapp;

import com.daemonize.daemonengine.utils.BoundedBufferQueue;

import org.junit.Assert;
import org.junit.Test;

import java.util.Queue;

public class BoundedBufferQueueTest {

    @Test
    public void test() {

        BoundedBufferQueue<Integer> queue = new BoundedBufferQueue<>(20);

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

    }

}
