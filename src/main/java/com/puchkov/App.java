package com.puchkov;

public class App {
    public static void main(String[] args) {
        RingBuffer<String> buffer = new RingBuffer<>(10);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                boolean itemAdded = false;
                while (!itemAdded) {
                    try {
                        buffer.put("Item " + i);
                        itemAdded = true;
                        System.out.println("Added: Item " + i + "; Buffer: " + buffer);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    } catch (BufferFullException e) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                String item;
                try {
                    item = buffer.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Taken: " + item + "; Buffer: " + buffer);
            }
        });

        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            buffer.close();
        }
    }
}
