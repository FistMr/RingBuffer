package com.puchkov;

public class App {
    public static void main(String[] args) {
        RingBuffer<String> buffer = new RingBuffer<>(3);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                buffer.put("Item " + i);
                System.out.println("Added: Item " + i + "; Buffer: " + buffer);
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                String item = buffer.get();
                System.out.println("Taken: " + item + "; Buffer: " + buffer);
            }
        });

        producer.start();
        consumer.start();
    }
}
