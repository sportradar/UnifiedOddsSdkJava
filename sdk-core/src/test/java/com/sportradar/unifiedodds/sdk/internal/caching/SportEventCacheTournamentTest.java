/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.SapiCategories.getSapiCategory;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.GERMANY_SCOTLAND_MATCH_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiSeasonCoverageInfos.fullyPopulatedSeasonCoverageInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournamentSchedules.Euro2024.euro2024TournamentSchedule;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders.summaryDataProvider;
import static com.sportradar.unifiedodds.sdk.impl.TournamentScheduleDataProviders.tournamentScheduleDataProvider;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.CurrentSeasonInfo;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.Tournament;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
import com.sportradar.unifiedodds.sdk.impl.TournamentSeasonsDataProviders;
import com.sportradar.unifiedodds.sdk.impl.assertions.CategorySummaryAssert;
import com.sportradar.unifiedodds.sdk.impl.assertions.SportSummaryAssert;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "ClassFanOutComplexity", "LineLength" })
class SportEventCacheTournamentTest {

    private static final String ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportEventCacheTournamentTest#englishAndChinese";
    private static final String TOURNAMENT_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.TournamentPropertyProviders#tournamentProperties";
    private static final String TOURNAMENT_PROPERTIES_POPULATED_FROM_CACHE =
        "com.sportradar.unifiedodds.sdk.internal.caching.TournamentPropertyProviders#tournamentPropertiesPopulatedFromCache";
    private static final String CURRENT_SEASON_INFO_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.CurrentSeasonInfoPropertyProviders#currentSeasonInfoProperties";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROPERTIES)
    void fetchesTournamentProperties(
        PropertyGetterFrom<Tournament> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = euro2024TournamentInfo();
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        sapiProperty.setOn(tournament);

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

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(tournamentEuro2024()))
            .providing(in(ENGLISH), sportFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val sportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(ENGLISH),
            false
        );

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROPERTIES)
    void fetchesTournamentPropertiesAndReturnsNullsForCatchExceptionStrategyWhenTournamentSummaryMissing(
        PropertyGetterFrom<Tournament> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = euro2024TournamentInfo();
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        sapiProperty.setOn(tournament);

        val summaryProvider = SummaryDataProviders.notProvidingAnyData();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(tournamentEuro2024()))
            .providing(in(ENGLISH), sportFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(ExceptionHandlingStrategy.Catch)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(ExceptionHandlingStrategy.Catch)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val sportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(ENGLISH),
            false
        );

        assertThat(property.getFrom(sportEvent)).isNull();
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROPERTIES_POPULATED_FROM_CACHE)
    void fetchesTournamentPropertiesAndThrowsForThrowExceptionStrategyWhenTournamentSummaryMissing(
        PropertyGetterFrom<Tournament> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = euro2024TournamentInfo();
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        sapiProperty.setOn(tournament);

        val summaryProvider = SummaryDataProviders.notProvidingAnyData();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(tournamentEuro2024()))
            .providing(in(ENGLISH), sportFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(ExceptionHandlingStrategy.Throw)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(ExceptionHandlingStrategy.Throw)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val sportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(ENGLISH),
            false
        );

        assertThatExceptionOfType(ObjectNotFoundException.class)
            .isThrownBy(() -> property.getFrom(sportEvent));
    }

    @ParameterizedTest
    @MethodSource(ENGLISH_AND_CHINESE)
    void fetchesSport(Locale language) throws Exception {
        val tournament = euro2024TournamentInfo();
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

        val sportsDataCache = SportsDataCaches
            .providing(in(language), categoryFrom(tournamentEuro2024()))
            .providing(in(language), sportFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val tournamentSportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(language),
            false
        );

        val sport = tournamentSportEvent.getSport();
        SportSummaryAssert
            .assertThat(sport, in(language))
            .hasIdAndNameEqualTo(getSapiSport(sportIdFrom(tournament), in(language)));

        val sportId = tournamentSportEvent.getSportId();
        assertThat(sportId).isEqualTo(sportIdFrom(tournament));
    }

    @ParameterizedTest
    @MethodSource("englishAndChinese")
    void fetchesCategory(Locale language) throws Exception {
        val tournament = euro2024TournamentInfo();
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

        val sportsDataCache = SportsDataCaches.providing(in(language), categoryFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val tournamentSportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(language),
            false
        );

        val category = tournamentSportEvent.getCategory();

        CategorySummaryAssert
            .assertThat(category, in(language))
            .hasIdNameAncCountryCodeEqualTo(
                getSapiCategory(categoryIdFrom(tournamentEuro2024()), in(ENGLISH)).getCategory()
            );
    }

    @ParameterizedTest
    @MethodSource(CURRENT_SEASON_INFO_PROPERTIES)
    void fetchesCurrentSeason(
        PropertyGetterFrom<CurrentSeasonInfo> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val language = ENGLISH;
        val tournament = euro2024TournamentInfo();
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        sapiProperty.setOn(tournament);

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

        val sportsDataCache = SportsDataCaches.providing(in(language), categoryFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val tournamentSportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(language),
            false
        );

        val currentSeason = tournamentSportEvent.getCurrentSeason();

        assertThat(property.getFrom(currentSeason)).isEqualTo(expected);
    }

    @Test
    void fetchesSeasons() throws Exception {
        val language = ENGLISH;
        val tournament = euro2024TournamentInfo();
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = SummaryDataProviders.providing(
            in(language),
            with(tournamentId.toString()),
            tournament
        );
        val seasonsProvider = TournamentSeasonsDataProviders.providing(
            in(ENGLISH),
            with(tournamentId.toString()),
            seasonFrom(tournament)
        );

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .withTournamentSeasons(seasonsProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches.providing(in(language), categoryFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(language)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(language)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val tournamentSportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(language),
            false
        );

        val seasons = tournamentSportEvent.getSeasons();

        assertThat(seasons).hasSize(1);
        assertThat(seasons).extracting("id").isEqualTo(singletonList(tournament.getSeason().getId()));
    }

    @Test
    void fetchesSchedule() throws Exception {
        val tournament = euro2024TournamentInfo();
        val tournamentId = Urn.parse(tournament.getTournament().getId());

        val summaryProvider = summaryDataProvider()
            .providing(in(ENGLISH), with(tournamentId.toString()), tournament)
            .providing(
                in(ENGLISH),
                with(GERMANY_SCOTLAND_MATCH_URN),
                soccerMatchGermanyScotlandEuro2024(ENGLISH)
            )
            .build();
        val tournamentScheduleProvider = tournamentScheduleDataProvider()
            .providing(in(ENGLISH), tournamentId.toString(), euro2024TournamentSchedule())
            .providing(
                in(ENGLISH),
                tournament.getTournament().getCurrentSeason().getId(),
                euro2024TournamentSchedule()
            )
            .build();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .withTournamentSchedule(tournamentScheduleProvider)
            .with(dataRouter)
            .build();

        val sportsDataCache = SportsDataCaches
            .providing(in(ENGLISH), categoryFrom(tournamentEuro2024()))
            .providing(in(ENGLISH), sportFrom(tournamentEuro2024()));

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .with(sportsDataCache)
            .build();

        val tournamentSportEvent = (Tournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(ENGLISH),
            false
        );

        val competitions = tournamentSportEvent.getSchedule();

        assertThat(competitions.get(0).getId()).isEqualTo(Urn.parse(GERMANY_SCOTLAND_MATCH_URN));
    }

    private SapiCategory categoryFrom(SapiTournamentExtended sapiTournamentExtended) {
        return sapiTournamentExtended.getCategory();
    }

    private Urn categoryIdFrom(SapiTournamentExtended sapiTournamentExtended) {
        return Urn.parse(sapiTournamentExtended.getCategory().getId());
    }

    private Urn sportIdFrom(SapiTournamentInfoEndpoint tournament) {
        return Urn.parse(tournament.getTournament().getSport().getId());
    }

    private SapiSport sportFrom(SapiTournamentExtended tournament) {
        return tournament.getSport();
    }

    private SapiTournamentInfoEndpoint seasonFrom(SapiTournamentInfoEndpoint tournament) {
        return tournament;
    }

    static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, CHINESE);
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "MagicNumber" })
class TournamentPropertyProviders {

    private static Stream<Arguments> tournamentProperties() {
        return Stream.concat(tournamentPropertiesPopulatedFromCache(), alwaysNullTournamentProperties());
    }

    private static Stream<Arguments> tournamentPropertiesPopulatedFromCache() {
        return Stream.of(
            arguments(
                "scheduledTime - available",
                SportEvent::getScheduledTime,
                p -> {
                    val date = LocalDateTime.of(2025, 3, 4, 2, 3, 4);
                    p.getTournament().setScheduled(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2025, 3, 4, 2, 3, 4).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "scheduledTime - missing",
                SportEvent::getScheduledTime,
                p -> p.getTournament().setScheduled(null),
                null
            ),
            arguments(
                "scheduledEndTime - available",
                SportEvent::getScheduledEndTime,
                p -> {
                    val date = LocalDateTime.of(2024, 5, 6, 7, 8, 9);
                    p.getTournament().setScheduledEnd(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2024, 5, 6, 7, 8, 9).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "scheduledEndTime - missing",
                SportEvent::getScheduledEndTime,
                p -> p.getTournament().setScheduledEnd(null),
                null
            ),
            arguments(
                "isExhibitionGames - true",
                Tournament::isExhibitionGames,
                p -> p.getTournament().setExhibitionGames(true),
                true
            ),
            arguments(
                "isExhibitionGames - false",
                Tournament::isExhibitionGames,
                p -> p.getTournament().setExhibitionGames(false),
                false
            ),
            arguments(
                "isExhibitionGames - null",
                Tournament::isExhibitionGames,
                p -> p.getTournament().setExhibitionGames(null),
                null
            )
        );
    }

    private static Stream<Arguments> alwaysNullTournamentProperties() {
        return Stream.of(
            arguments(
                "replacedBy - always null because cache item always returns null",
                SportEvent::getReplacedBy,
                p -> {},
                null
            ),
            arguments(
                "startTimeTbd - always false because cache item always returns Optional.empty()",
                SportEvent::isStartTimeTbd,
                p -> {},
                null
            )
        );
    }

    private static SapiReferenceIds addReference(String reference, String id) {
        SapiReferenceIds referenceIds = new SapiReferenceIds();
        SapiReferenceIds.SapiReferenceId referenceId = new SapiReferenceIds.SapiReferenceId();
        referenceId.setName(reference);
        referenceId.setValue(id);
        referenceIds.getReferenceId().add(referenceId);
        return referenceIds;
    }

    static Arguments arguments(
        String propertyName,
        PropertyGetterFrom<Tournament> propertyGetter,
        PropertySetterTo<SapiTournamentInfoEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "MagicNumber" })
class CurrentSeasonInfoPropertyProviders {

    private static Stream<Arguments> currentSeasonInfoProperties() {
        return Stream.of(
            arguments(
                "id",
                CurrentSeasonInfo::getId,
                p -> p.getTournament().getCurrentSeason().setId("sr:season:23"),
                Urn.parse("sr:season:23")
            ),
            arguments(
                "startDate - missing",
                CurrentSeasonInfo::getStartDate,
                p -> p.getTournament().getCurrentSeason().setStartDate(null),
                null
            ),
            arguments(
                "startDate - available",
                CurrentSeasonInfo::getStartDate,
                p -> {
                    val date = LocalDateTime.of(2021, 5, 6, 7, 8, 9);
                    p.getTournament().getCurrentSeason().setStartDate(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2021, 5, 6, 7, 8, 9).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "endDate - missing",
                CurrentSeasonInfo::getEndDate,
                p -> p.getTournament().getCurrentSeason().setEndDate(null),
                null
            ),
            arguments(
                "endDate - available",
                CurrentSeasonInfo::getEndDate,
                p -> {
                    val date = LocalDateTime.of(2019, 1, 6, 7, 8, 9);
                    p.getTournament().getCurrentSeason().setEndDate(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2019, 1, 6, 7, 8, 9).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "year - missing",
                CurrentSeasonInfo::getYear,
                p -> p.getTournament().getCurrentSeason().setYear(null),
                null
            ),
            arguments(
                "year - available",
                CurrentSeasonInfo::getYear,
                p -> p.getTournament().getCurrentSeason().setYear("1999"),
                "1999"
            ),
            arguments(
                "name - available",
                s -> s.getName(ENGLISH),
                p -> p.getTournament().getCurrentSeason().setName("Important Season"),
                "Important Season"
            ),
            arguments(
                "name - empty",
                s -> s.getName(ENGLISH),
                p -> p.getTournament().getCurrentSeason().setName(""),
                ""
            ),
            arguments(
                "name - missing",
                s -> s.getName(ENGLISH),
                p -> p.getTournament().getCurrentSeason().setName(null),
                null
            ),
            arguments(
                "names - missing",
                CurrentSeasonInfo::getNames,
                p -> p.getTournament().getCurrentSeason().setName(null),
                ImmutableMap.of()
            ),
            arguments(
                "names - available",
                CurrentSeasonInfo::getNames,
                p -> p.getTournament().getCurrentSeason().setName("Euro 2024"),
                ImmutableMap.of(ENGLISH, "Euro 2024")
            ),
            arguments(
                "coverage - missing",
                CurrentSeasonInfo::getCoverage,
                p -> p.getTournament().setSeasonCoverageInfo(null),
                null
            ),
            arguments(
                "coverage - id",
                cs -> cs.getCoverage().getSeasonId(),
                p -> p.getTournament().setSeasonCoverageInfo(fullyPopulatedSeasonCoverageInfo()),
                Urn.parse(fullyPopulatedSeasonCoverageInfo().getSeasonId())
            ),
            arguments(
                "coverage - scheduled",
                cs -> cs.getCoverage().getScheduled(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setScheduled(240);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                240
            ),
            arguments(
                "coverage - played",
                cs -> cs.getCoverage().getScheduled(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setScheduled(120);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                120
            ),
            arguments(
                "coverage - maxCovered - available",
                cs -> cs.getCoverage().getMaxCovered(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCovered(2);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                2
            ),
            arguments(
                "coverage - maxCovered - missing",
                cs -> cs.getCoverage().getMaxCovered(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCovered(null);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                null
            ),
            arguments(
                "coverage - maxCoverageLevel - available",
                cs -> cs.getCoverage().getMaxCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCoverageLevel("gold");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                "gold"
            ),
            arguments(
                "coverage - maxCoverageLevel - missing",
                cs -> cs.getCoverage().getMaxCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCoverageLevel(null);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                null
            ),
            arguments(
                "coverage - maxCoverageLevel - empty",
                cs -> cs.getCoverage().getMaxCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCoverageLevel("");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                ""
            ),
            arguments(
                "coverage - minCoverageLevel - available",
                cs -> cs.getCoverage().getMinCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMinCoverageLevel("gold");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                "gold"
            ),
            arguments(
                "coverage - minCoverageLevel - missing",
                cs -> cs.getCoverage().getMinCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMinCoverageLevel(null);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                null
            ),
            arguments(
                "coverage - minCoverageLevel - empty",
                cs -> cs.getCoverage().getMinCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMinCoverageLevel("");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                ""
            )
        );
    }

    static Arguments arguments(
        String propertyName,
        PropertyGetterFrom<CurrentSeasonInfo> propertyGetter,
        PropertySetterTo<SapiTournamentInfoEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
