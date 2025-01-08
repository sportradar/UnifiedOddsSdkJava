/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.recovery;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.Producer;
import com.sportradar.uf.sportsapi.datamodel.Producers;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import java.util.Arrays;
import lombok.SneakyThrows;

public class ProducerDataProviders {

    @SneakyThrows
    public static DataProvider<Producers> providingSuccessfully(Producer... producers) {
        DataProvider<Producers> producersDataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        Producers producersToProvide = new Producers();

        Arrays.stream(producers).forEach(producersToProvide.getProducer()::add);

        producersToProvide.setResponseCode(ResponseCode.OK);
        doReturn(producersToProvide).when(producersDataProvider).getData();
        return producersDataProvider;
    }
}
