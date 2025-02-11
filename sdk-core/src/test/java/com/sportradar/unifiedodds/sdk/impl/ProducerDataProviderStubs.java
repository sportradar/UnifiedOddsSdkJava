/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.impl.ProducerData;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import java.util.List;

public class ProducerDataProviderStubs {

    private ProducerDataProviderStubs() {}

    public static ProducerDataProvider anyProducerDataProvider() {
        return new ProducerDataProvider() {
            @Override
            public List<ProducerData> getAvailableProducers() {
                return asList();
            }
        };
    }

    public static ProducerDataProvider providerOfSingleEmptyProducer() {
        ProducerDataProvider provider = mock(ProducerDataProvider.class);
        when(provider.getAvailableProducers()).thenReturn(asList(mock(ProducerData.class)));
        return provider;
    }
}
