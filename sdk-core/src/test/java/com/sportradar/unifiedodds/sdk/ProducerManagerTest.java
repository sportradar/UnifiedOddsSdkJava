/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.di.TestingModule;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "ConstantName", "MagicNumber" })
public class ProducerManagerTest {

    private static final int PID = 1;

    private static final Injector injector = Guice.createInjector(
        Modules.override(new MockedMasterModule()).with(new TestingModule())
    );

    private static final SdkProducerManager producerManager = injector.getInstance(SdkProducerManager.class);

    @Test
    public void getAvailableProducers() {
        Map<Integer, Producer> availableProducers = producerManager.getAvailableProducers();

        assertNotNull(availableProducers);
        assertEquals(4, availableProducers.size());

        verifyProducer(availableProducers.get(PID), PID, "LO");
        verifyProducer(availableProducers.get(3), 3, "Ctrl");
        verifyProducer(availableProducers.get(4), 4, "BetPal");
        verifyProducer(availableProducers.get(5), 5, "PremiumCricket");
    }

    //available == active
    @Test
    public void getActiveProducers() {
        Map<Integer, Producer> activeProducers = producerManager.getActiveProducers();

        activeProducers.values().forEach(producer -> assertTrue(producer.isAvailable()));
    }

    @Test
    public void getsExistingProducer() {
        verifyProducer(producerManager.getProducer(PID), PID, "LO");
    }

    @Test
    public void generatesUnknownProducer() {
        verifyProducer(producerManager.getProducer(2), 2, "Unknown");
    }

    @Test
    public void disablesAndEnablesProducer() {
        //Disable
        producerManager.disableProducer(PID);
        assertFalse(producerManager.isProducerEnabled(PID));

        //Enable
        producerManager.enableProducer(PID);
        assertTrue(producerManager.isProducerEnabled(PID));
    }

    //max recovery window in producers.xml is set to 3 hours for all producers

    @Test
    public void setsLastMessageReceivedTimestamp() {
        long timestamp = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);

        producerManager.setProducerRecoveryFromTimestamp(PID, timestamp);

        assertEquals(timestamp, producerManager.getProducer(PID).getTimestampForRecovery());
    }

    @Test
    public void throwsWhenTimestampLargerThanMaxRecoveryWindow() {
        long timestamp = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(4, TimeUnit.HOURS);

        assertThatThrownBy(() -> producerManager.setProducerRecoveryFromTimestamp(PID, timestamp))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void marksProducerDown() {
        producerManager.setProducerDown(PID, false);
        assertFalse(producerManager.isProducerDown(PID));

        producerManager.setProducerDown(PID, true);
        assertTrue(producerManager.isProducerDown(PID));
    }

    //Helpers

    private static void verifyProducer(Producer producer, int id, String name) {
        assertEquals(id, producer.getId());
        assertEquals(name, producer.getName());
        assertTrue(producer.isEnabled());
    }
}
