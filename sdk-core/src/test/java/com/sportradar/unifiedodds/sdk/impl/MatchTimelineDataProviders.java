/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;
import lombok.val;

public class MatchTimelineDataProviders {

    @SneakyThrows
    public static DataProvider<SapiMatchTimelineEndpoint> notProvidingAnyData() {
        DataProvider<SapiMatchTimelineEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<SapiMatchTimelineEndpoint> providing(
        LanguageHolder language,
        SapiMatchTimelineEndpoint timeline
    ) {
        val dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        val sportEventId = timeline.getSportEvent().getId();
        doReturn(timeline).when(dataProvider).getData(eq(language.get()), eq(sportEventId));
        doReturn(anyHttpUrl().toString())
            .when(dataProvider)
            .getFinalUrl(eq(language.get()), eq(sportEventId));
        return dataProvider;
    }
}
