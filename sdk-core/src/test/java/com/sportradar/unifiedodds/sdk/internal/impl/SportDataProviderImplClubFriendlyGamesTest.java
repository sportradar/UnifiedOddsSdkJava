/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.ClubFriendlyGamesTournament.clubFriendlyGamesTournament;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SimpleTournaments.ClubFriendlyGames.clubFriendlyGames;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SimpleTournaments.TenerifeWomensOpen2025Golf.tenerifeWomensOpen2025Golf;
import static com.sportradar.unifiedodds.sdk.impl.RequestOptionsArgumentMatchers.executionPathEq;
import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptionsProviders.nonTimeCriticalRequestOptions;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviders.stubbingOutSportDataProvider;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "MagicNumber", "LineLength" })
class SportDataProviderImplClubFriendlyGamesTest {

    private static final String TOURNAMENT_PROVIDERS_FOR_DEFAULT_LANGUAGE =
        "com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviderImplClubFriendlyGamesTest#tournamentProvidersWithDefaultLanguage";
    private static final String TOURNAMENT_PROVIDERS_FOR_GIVEN_LANGUAGE =
        "com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviderImplClubFriendlyGamesTest#tournamentProvidersWithGivenLanguage";
    private static final String NON_CLUB_FRIENDLY_TOURNAMENT_PROVIDERS_FOR_GIVEN_LANGUAGE =
        "com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviderImplClubFriendlyGamesTest#nonClubFriendlyTournamentProvidersWithGivenLanguage";

    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROVIDERS_FOR_DEFAULT_LANGUAGE)
    void getsClubFriendlyGamesWithDefaultLanguageAndImmediatelyFetchesSummaryWithNonTimeCriticalExecutionPath(
        SportEventGetterWithDefaultLanguage sportEventGetter,
        SapiTournamentInfoEndpoint tournament
    ) throws Exception {
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.providing(
            in(ENGLISH),
            with(tournamentId.toString()),
            tournament
        );

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val provider = stubbingOutSportDataProvider()
            .withDesiredLocale(ENGLISH)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEntityFactory)
            .build();

        val sportEvent = sportEventGetter.getSportEvent(from(provider), with(tournamentId));

        assertThat(sportEvent).isNotNull();

        verify(summaryProvider)
            .getData(
                executionPathEq(nonTimeCriticalRequestOptions()),
                eq(ENGLISH),
                eq(tournamentId.toString())
            );
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROVIDERS_FOR_GIVEN_LANGUAGE)
    void getsClubFriendlyGamesWithGivenLanguageAndImmediatelyFetchesSummaryWithNonTimeCriticalExecutionPath(
        SportEventGetterWithGivenLanguage sportEventGetter,
        Locale language,
        SapiTournamentInfoEndpoint tournament
    ) throws Exception {
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.providing(
            in(language),
            with(tournamentId.toString()),
            tournament
        );

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .build();

        val provider = stubbingOutSportDataProvider()
            .withDesiredLocale(language)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEntityFactory)
            .build();

        val sportEvent = sportEventGetter.getSportEvent(from(provider), with(tournamentId), in(language));

        assertThat(sportEvent).isNotNull();

        verify(summaryProvider)
            .getData(
                executionPathEq(nonTimeCriticalRequestOptions()),
                eq(language),
                eq(tournamentId.toString())
            );
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROVIDERS_FOR_GIVEN_LANGUAGE)
    void onAttemptToGetClubFriendlyGamesWithGivenLanguageFailsWhileFetchingSummaryWhenNotFound(
        SportEventGetterWithGivenLanguage sportEventGetter,
        Locale language
    ) {
        val tournament = clubFriendlyGames(language);
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.notProvidingAnyData();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .build();

        val provider = stubbingOutSportDataProvider()
            .withDesiredLocale(language)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEntityFactory)
            .build();

        assertThatExceptionOfType(ObjectNotFoundException.class)
            .isThrownBy(() -> sportEventGetter.getSportEvent(from(provider), with(tournamentId), in(language))
            );
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROVIDERS_FOR_DEFAULT_LANGUAGE)
    void getsNonClubFriendlyGamesWithDefaultLanguageAndDoesNoImmediateSummaryFetching(
        SportEventGetterWithDefaultLanguage sportEventGetter
    ) {
        val tournament = tenerifeWomensOpen2025Golf(ENGLISH);
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.providing(
            in(ENGLISH),
            with(tournamentId.toString()),
            tournament
        );

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val provider = stubbingOutSportDataProvider()
            .withDesiredLocale(ENGLISH)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEntityFactory)
            .build();

        val sportEvent = sportEventGetter.getSportEvent(from(provider), with(tournamentId));

        assertThat(sportEvent).isNotNull();

        verifyNoInteractions(summaryProvider);
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROVIDERS_FOR_DEFAULT_LANGUAGE)
    void onAttemptToGetClubFriendlyGamesWithDefaultLanguageFailsWhileFetchingSummaryWhenNotFound(
        SportEventGetterWithDefaultLanguage sportEventGetter
    ) {
        val tournament = clubFriendlyGames(ENGLISH);
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.notProvidingAnyData();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val provider = stubbingOutSportDataProvider()
            .withDesiredLocale(ENGLISH)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEntityFactory)
            .build();

        assertThatExceptionOfType(ObjectNotFoundException.class)
            .isThrownBy(() -> sportEventGetter.getSportEvent(from(provider), with(tournamentId)));
    }

    @ParameterizedTest
    @MethodSource(NON_CLUB_FRIENDLY_TOURNAMENT_PROVIDERS_FOR_GIVEN_LANGUAGE)
    void getsNonClubFriendlyGamesWithGivenLanguageAndDoesNoImmediateSummaryFetching(
        SportEventGetterWithGivenLanguage sportEventGetter,
        Locale language,
        SapiTournamentInfoEndpoint tournament
    ) {
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.providing(
            in(language),
            with(tournamentId.toString()),
            tournament
        );

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .build();

        val provider = stubbingOutSportDataProvider()
            .withDesiredLocale(language)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEntityFactory)
            .build();

        val sportEvent = sportEventGetter.getSportEvent(from(provider), with(tournamentId), in(language));

        assertThat(sportEvent).isNotNull();

        verifyNoInteractions(summaryProvider);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> tournamentProvidersWithDefaultLanguage() {
        return Stream.of(
            arguments(
                "getLongTermEvent (simple_tournament:86)",
                SportDataProvider::getLongTermEvent,
                clubFriendlyGames(ENGLISH)
            ),
            arguments(
                "getSportEvent (simple_tournamentL86)",
                SportDataProvider::getSportEvent,
                clubFriendlyGames(ENGLISH)
            ),
            arguments(
                "getLongTermEvent (tournament:853)",
                SportDataProvider::getLongTermEvent,
                clubFriendlyGamesTournament(ENGLISH)
            ),
            arguments(
                "getSportEvent (tournament:853)",
                SportDataProvider::getSportEvent,
                clubFriendlyGamesTournament(ENGLISH)
            )
        );
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> tournamentProvidersWithGivenLanguage() {
        return Stream.of(
            arguments(
                "getLongTermEvent EN (simple_tournament:86)",
                ENGLISH,
                getLongTermEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGames(ENGLISH)
            ),
            arguments(
                "getLongTermEvent ZH (simple_tournament:86)",
                CHINESE,
                getLongTermEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGames(ENGLISH)
            ),
            arguments(
                "getSportEvent EN (simple_tournament:86)",
                ENGLISH,
                getSportEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGames(ENGLISH)
            ),
            arguments(
                "getSportEvent ZH (simple_tournament:86)",
                CHINESE,
                getSportEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGames(ENGLISH)
            ),
            arguments(
                "getLongTermEvent EN (tournament:853)",
                ENGLISH,
                getLongTermEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGamesTournament(ENGLISH)
            ),
            arguments(
                "getLongTermEvent ZH (tournament:853)",
                CHINESE,
                getLongTermEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGamesTournament(CHINESE)
            ),
            arguments(
                "getSportEvent EN (tournament:853)",
                ENGLISH,
                getSportEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGamesTournament(ENGLISH)
            ),
            arguments(
                "getSportEvent ZH (tournament:853)",
                CHINESE,
                getSportEventFromSportDataProviderWithGivenLanguage(),
                clubFriendlyGamesTournament(CHINESE)
            )
        );
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> nonClubFriendlyTournamentProvidersWithGivenLanguage() {
        return Stream.of(
            arguments(
                "getLongTermEvent EN",
                ENGLISH,
                getLongTermEventFromSportDataProviderWithGivenLanguage(),
                tenerifeWomensOpen2025Golf(ENGLISH)
            ),
            arguments(
                "getLongTermEvent ZH",
                CHINESE,
                getLongTermEventFromSportDataProviderWithGivenLanguage(),
                tenerifeWomensOpen2025Golf(CHINESE)
            ),
            arguments(
                "getSportEvent EN",
                ENGLISH,
                getSportEventFromSportDataProviderWithGivenLanguage(),
                tenerifeWomensOpen2025Golf(ENGLISH)
            )
        );
    }

    @NotNull
    private static SportEventGetterWithGivenLanguage getSportEventFromSportDataProviderWithGivenLanguage() {
        return (provider, id, language) -> provider.getSportEvent(id, language.get());
    }

    private static SportEventGetterWithGivenLanguage getLongTermEventFromSportDataProviderWithGivenLanguage() {
        return (provider, id, language) -> provider.getLongTermEvent(id, language.get());
    }

    private static Arguments arguments(
        String name,
        SportEventGetterWithDefaultLanguage sportEventProvider,
        SapiTournamentInfoEndpoint tournament
    ) {
        return Arguments.of(named(name, sportEventProvider), tournament);
    }

    private static Arguments arguments(
        String name,
        Locale language,
        SportEventGetterWithGivenLanguage sportEventProvider,
        SapiTournamentInfoEndpoint tournament
    ) {
        return Arguments.of(named(name, sportEventProvider), language, tournament);
    }

    interface SportEventGetterWithDefaultLanguage {
        SportEvent getSportEvent(SportDataProvider provider, Urn urn);
    }

    interface SportEventGetterWithGivenLanguage {
        SportEvent getSportEvent(SportDataProvider provider, Urn urn, LanguageHolder language);
    }
}
