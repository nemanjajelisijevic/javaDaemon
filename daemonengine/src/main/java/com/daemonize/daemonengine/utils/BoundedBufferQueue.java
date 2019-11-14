package com.daemonize.daemonengine.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BoundedBufferQueue<T> implements Queue<T> {

    private final int capacity;
    private final Object[] array;

    private int head = 0;
    private int tail = 0;

    public BoundedBufferQueue(int capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException("Invalid size arg: " + capacity + " (must be > 0)");
        this.capacity = capacity + 1;
        this.array = new Object[this.capacity];
    }

    @Override
    public boolean add(T element) {
        if (!offer(element))
            throw new IllegalStateException("Queue full");
        return true;
    }

    @Override
    public boolean offer(T element) {
        int toBeTail = (tail + 1) % (capacity);
        if (toBeTail != head) {
            array[tail] = element;
            tail = toBeTail;
            return true;
        } else
            return false;
    }

    @Override
    public T remove() {
        T ret = poll();
        if (ret == null)
            throw new NoSuchElementException("Queue empty");
        else
            return ret;
    }

    @Override
    public T poll() {
        if (head == tail)
            return null;
        else {
            T ret = (T) array[head];
            head = (head + 1) % capacity;
            return ret;
        }
    }

    @Override
    public T element() {
        T ret = peek();
        if (ret == null)
            throw new NoSuchElementException("Queue empty");
        else
            return ret;
    }

    @Override
    public T peek() {
        if (head == tail)
            return null;
        else
            return (T) array[head];
    }

    @Override
    public int size() {
        if (head == tail)
            return 0;
        else if (tail > head)
            return (tail - head);
        else
            return (capacity - head) + tail;
    }

    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        if (tail >= head)
            return  Arrays.copyOfRange(this.array, head, tail);
        else {
            T[] partOne = (T[]) Arrays.copyOfRange(this.array, head, capacity);
            T[] partTwo = (T[]) Arrays.copyOfRange(this.array, 0, tail);

            Object[] ret = new Object[partOne.length + partTwo.length];

            int i = 0;
            for (; i< partOne.length; ++i)
                ret[i] = partOne[i];

            for (int j = 0; j < partTwo.length; ++j)
                ret[i++] = partTwo[j];

            return ret;
        }
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        head = 0;
        tail = 0;
    }

    //DEBUG
    public int getHead() {
        return head;
    }

    public int getTail() {
        return tail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < this.capacity; ++i) {
            sb.append("Buffer[" + i + "]: " + array[i] + ((head  == i) ? " - HEAD": "" ) + ((tail == i) ? " - TAIL" : ""));
            sb.append("\n");
        }
        return sb.toString();
    }
}
