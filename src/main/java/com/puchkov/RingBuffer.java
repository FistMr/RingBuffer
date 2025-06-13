package com.puchkov;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RingBuffer<T> implements AutoCloseable {
    private final T[] buffer;
    private final int capacity;
    private volatile boolean isClosed = false;

    private int start = 0;
    private int end = 0;
    private volatile int count = 0;

    private final Lock lock = new ReentrantLock();

    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    public void put(T item) throws InterruptedException, BufferFullException {
        if (item == null) {
            throw new NullPointerException("Item cannot be null");
        }
        lock.lockInterruptibly();
        try {
            checkIfClosed();
            if (isFull()) {
                throw new BufferFullException("Buffer is full");
            }
            buffer[start] = item;
            start = (start + 1) % capacity;
            count++;
        } finally {
            lock.unlock();
        }
    }


    public T get() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            checkIfClosed();
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

    public T peek() throws InterruptedException {
        try {
            lock.lockInterruptibly();
            checkIfClosed();
            if (isEmpty()) {
                return null;
            }
            return buffer[end];
        } finally {
            lock.unlock();
        }
    }

    public void clear() throws InterruptedException {
        try {
            lock.lockInterruptibly();
            checkIfClosed();

            for (int i = 0; i < capacity; i++) {
                buffer[i] = null;
            }
            start = 0;
            end = 0;
            count = 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            isClosed = true;
            for (int i = 0; i < capacity; i++) {
                buffer[i] = null;
            }
            start = 0;
            end = 0;
            count = 0;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return count;
    }


    public boolean isEmpty() {
        checkIfClosed();
        return count == 0;
    }

    public boolean isFull() {
        checkIfClosed();
        return count == capacity;
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

    private void checkIfClosed() {
        if (isClosed) {
            throw new IllegalStateException("RingBuffer is closed");
        }
    }
}
