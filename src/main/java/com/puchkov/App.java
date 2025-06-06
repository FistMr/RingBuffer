package com.puchkov;
public class App {
    public static void main(String[] args) {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(5);
        for (int i = 1; i <= 60; i++) {
            ringBuffer.put(i);
            System.out.println(ringBuffer);
        }
        for (int i = 1; i <= 6; i++) {
            System.out.println(ringBuffer.get());
            System.out.println(ringBuffer);
        }

    }
}
