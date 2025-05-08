/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import lombok.SneakyThrows;

public class MatchTimelineDataProviders {

    @SneakyThrows
    public static DataProvider<SapiMatchTimelineEndpoint> notProvidingAnyData() {
        DataProvider<SapiMatchTimelineEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        return dataProvider;
    }
}
