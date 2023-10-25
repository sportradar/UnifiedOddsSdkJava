/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SdkConnectionIT.checkListContainsString;
import static com.sportradar.unifiedodds.sdk.conn.SdkConnectionIT.waitAndCheckTillTimeout;

import java.util.function.Consumer;

public class Scenario {

    private UofConnListener listener;
    private Runnable trigger;

    public Scenario(UofConnListener listener, Runnable trigger) {
        this.listener = listener;
        this.trigger = trigger;
    }

    void then(Consumer<Expectations> expectations) {
        listener.CalledEvents.clear();

        trigger.run();

        expectations.accept(new Expectations(listener));
    }

    static class Factory {

        private UofConnListener listener;

        public Factory(UofConnListener listener) {
            this.listener = listener;
        }

        Scenario when(Runnable trigger) {
            return new Scenario(listener, trigger);
        }
    }

    static class Expectations {

        private static final int CONDITION_FULFILLMENT_TIMEOUT_IN_MILLIS = 60000;
        private static final int CHECK_CONDITION_EVERY_MILLIS = 3000;

        private UofConnListener listener;

        Expectations(UofConnListener listener) {
            this.listener = listener;
        }

        Expectations virtualProducerDownDueToConnectionDown() {
            waitAndCheckTillTimeout(
                w -> checkListContainsString(listener.CalledEvents, "Producer VF is down: ConnectionDown", 1),
                "Producer VF is down: ConnectionDown",
                CHECK_CONDITION_EVERY_MILLIS,
                CONDITION_FULFILLMENT_TIMEOUT_IN_MILLIS
            );
            return this;
        }

        Expectations prematchProducerDownDueToConnectionDown() {
            waitAndCheckTillTimeout(
                w ->
                    checkListContainsString(
                        listener.CalledEvents,
                        "Producer Ctrl is down: ConnectionDown",
                        1
                    ),
                "Producer Ctrl is down: ConnectionDown",
                CHECK_CONDITION_EVERY_MILLIS,
                CONDITION_FULFILLMENT_TIMEOUT_IN_MILLIS
            );
            return this;
        }

        Expectations liveOddsProducerDownDueToConnectionDown() {
            waitAndCheckTillTimeout(
                w -> checkListContainsString(listener.CalledEvents, "Producer LO is down: ConnectionDown", 1),
                "Producer LO is down: ConnectionDown",
                CHECK_CONDITION_EVERY_MILLIS,
                CONDITION_FULFILLMENT_TIMEOUT_IN_MILLIS
            );
            return this;
        }

        Expectations connectionToBeDown() {
            waitAndCheckTillTimeout(
                w1 -> checkListContainsString(listener.CalledEvents, "Connection to the feed lost", 1),
                "Connection to the feed lost",
                CHECK_CONDITION_EVERY_MILLIS,
                CONDITION_FULFILLMENT_TIMEOUT_IN_MILLIS
            );
            return this;
        }

        Expectations and() {
            return this;
        }
    }
}
