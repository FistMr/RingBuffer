package com.puchkov;

public class RingBuffer<T> {
    private final T[] buffer;
    private final int capacity;

    private int start = 0;
    private int end = 0;
    private int count = 0;

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
        buffer[start] = item;
        if (isFull()) {
            end = (end + 1) % capacity;
        } else {
            count++;
        }
        start = (start + 1) % capacity;

    }


    public T get() {
        if (isEmpty()) {
            return null;
        }
        T item = buffer[end];
        buffer[end] = null;
        end = (end + 1) % capacity;
        count--;
        return item;
    }


    public int size() {
        return count;
    }


    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == capacity;
    }

    @Override
    public String toString() {
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
    }
}
