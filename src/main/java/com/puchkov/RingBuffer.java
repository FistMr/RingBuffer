package com.puchkov;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RingBuffer<T> {
    private final T[] buffer;
    private final int capacity;

    private int start = 0;
    private int end = 0;
    private int count = 0;

    private final Lock lock = new ReentrantLock();

    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    public void put(T item) {
        if (item == null) {
            throw new NullPointerException("Item cannot be null");
        }
        lock.lock();
        try {
            buffer[start] = item;
            if (isFull()) {
                end = (end + 1) % capacity;
            } else {
                count++;
            }
            start = (start + 1) % capacity;
        } finally {
            lock.unlock();
        }
    }


    public T get() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }
            T item = buffer[end];
            buffer[end] = null;
            end = (end + 1) % capacity;
            count--;
            return item;
        } finally {
            lock.unlock();
        }

    }


    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }


    public boolean isEmpty() {
        lock.lock();
        try {
            return count == 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return count == capacity;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            if (isEmpty()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder("[");
            int current = end;
            for (int i = 0; i < count; i++) {
                sb.append(buffer[current]);
                if (i < count - 1) {
                    sb.append(", ");
                }
                current = (current + 1) % capacity;
            }
            sb.append("]");
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }
}
