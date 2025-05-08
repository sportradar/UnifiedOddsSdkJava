/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiTournamentSchedule;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class TournamentScheduleDataProviders {

    public static TournamentScheduleDataProviderBuilder tournamentScheduleDataProvider() {
        return new TournamentScheduleDataProviderBuilder();
    }

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String tournamentId,
        SapiTournamentSchedule schedule
    ) {
        return new TournamentScheduleDataProviderBuilder()
            .providing(language, tournamentId, schedule)
            .build();
    }

    public static class TournamentScheduleDataProviderBuilder {

        private final DataProvider<Object> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );

        @SneakyThrows
        public TournamentScheduleDataProviderBuilder providing(
            LanguageHolder language,
            String tournamentOrSeasonId,
            SapiTournamentSchedule schedule
        ) {
            doReturn(schedule).when(dataProvider).getData(language.get(), tournamentOrSeasonId);
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(language.get(), tournamentOrSeasonId);
            return this;
        }

        public DataProvider<Object> build() {
            return dataProvider;
        }
    }
}
