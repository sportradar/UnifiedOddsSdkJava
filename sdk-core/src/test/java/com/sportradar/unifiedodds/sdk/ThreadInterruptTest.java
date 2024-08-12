package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals", "LambdaBodyLength" })
public class ThreadInterruptTest {

    @Test
    public void test_interrupt() {
        assertThatNoException()
            .isThrownBy(() -> {
                Thread t = new Thread(this::run);
                System.out.println(
                    "Name : " +
                    t.getName() +
                    ", state : " +
                    t.getState() +
                    ", interrupted? " +
                    t.isInterrupted()
                );
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    System.out.println(
                        "Name : " +
                        t.getName() +
                        ", state : " +
                        t.getState() +
                        ", interrupted? " +
                        t.isInterrupted()
                    );
                    throw new RuntimeException(e);
                }

                System.out.println(
                    "Name : " +
                    t.getName() +
                    ", state : " +
                    t.getState() +
                    ", interrupted? " +
                    t.isInterrupted()
                );
            });
    }

    public void run() {
        System.out.println(
            "Name : " + Thread.currentThread().getName() + ", state : " + Thread.currentThread().getState()
        );

        try {
            while (true) {
                Thread.sleep(1000);
                System.out.println(
                    "Name : " +
                    Thread.currentThread().getName() +
                    ", state : " +
                    Thread.currentThread().getState()
                );

                // Strange thing happens, so we call interrupt
                Thread.currentThread().interrupt();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Exiting....");
        }
    }
}
