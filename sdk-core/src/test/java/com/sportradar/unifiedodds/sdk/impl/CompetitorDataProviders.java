/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiSimpleTeamProfileEndpoint;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CompetitorDataProviders {

    @SneakyThrows
    public static DataProvider<SapiCompetitorProfileEndpoint> failingFirstAndThenProviding(
        LanguageHolder language,
        String sportEventId,
        SapiCompetitorProfileEndpoint competitor
    ) {
        DataProvider<SapiCompetitorProfileEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doAnswer(firstFailThanReturn(competitor)).when(dataProvider).getData(language.get(), sportEventId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
        return dataProvider;
    }

    private static Answer<SapiCompetitorProfileEndpoint> firstFailThanReturn(
        SapiCompetitorProfileEndpoint result
    ) {
        return new Answer<SapiCompetitorProfileEndpoint>() {
            private boolean first = true;

            @Override
            public SapiCompetitorProfileEndpoint answer(InvocationOnMock invocation) throws Throwable {
                if (first) {
                    first = false;
                    throw new DataProviderException("stubbed to throw");
                } else {
                    return result;
                }
            }
        };
    }

    @SneakyThrows
    public static DataProvider<SapiCompetitorProfileEndpoint> providing(
        LanguageHolder language,
        String sportEventId,
        SapiCompetitorProfileEndpoint competitor
    ) {
        DataProvider<SapiCompetitorProfileEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(competitor).when(dataProvider).getData(language.get(), sportEventId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<SapiSimpleTeamProfileEndpoint> providing(
        LanguageHolder language,
        String teamId,
        SapiSimpleTeamProfileEndpoint competitor
    ) {
        DataProvider<SapiSimpleTeamProfileEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(competitor).when(dataProvider).getData(language.get(), teamId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), teamId);
        return dataProvider;
    }

    public static DataProvider<SapiCompetitorProfileEndpoint> failingToProvide(
        LanguageHolder language,
        String sportEventId
    ) throws DataProviderException {
        DataProvider<SapiCompetitorProfileEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doThrow(DataProviderException.class).when(dataProvider).getData(language.get(), sportEventId);
        return dataProvider;
    }
}
