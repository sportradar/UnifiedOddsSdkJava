/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;
import lombok.val;

public class TournamentSeasonsDataProviders {

    @SneakyThrows
    public static DataProvider<SapiTournamentSeasons> providing(
        LanguageHolder language,
        String tournamentId,
        SapiTournamentInfoEndpoint tournament
    ) {
        val seasons = new SapiTournamentSeasons();
        seasons.setTournament(tournament.getTournament());
        SapiSeasons sapiSeasons = new SapiSeasons();
        sapiSeasons.getSeason().add(tournament.getSeason());
        seasons.setSeasons(sapiSeasons);
        DataProvider<SapiTournamentSeasons> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(seasons).when(dataProvider).getData(language.get(), tournamentId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), tournamentId);
        return dataProvider;
    }
}
