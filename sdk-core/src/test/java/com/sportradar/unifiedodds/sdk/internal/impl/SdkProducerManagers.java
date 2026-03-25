/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

public class SdkProducerManagers {

    public static SdkProducerManager backedByProducer(Producer producer) {
        SdkProducerManager producerManager = mock(SdkProducerManager.class);
        int id = producer.getId();
        boolean isAvailable = producer.isAvailable();
        boolean isEnabled = producer.isEnabled();

        if (isAvailable) {
            when(producerManager.getActiveProducers()).thenReturn(ImmutableMap.of(id, producer));
        }
        when(producerManager.isProducerEnabled(id)).thenReturn(isEnabled);
        when(producerManager.getProducer(id)).thenReturn(producer);
        return producerManager;
    }
}
