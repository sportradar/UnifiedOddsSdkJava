/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiSeasonCoverageInfos.fullyPopulatedSeasonCoverageInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentExtended;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Season;
import com.sportradar.unifiedodds.sdk.entities.SeasonCoverage;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "ClassFanOutComplexity", "LineLength" })
class SportEventCacheSeasonTest {

    private static final String SEASON_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.SeasonPropertyProviders#seasonProperties";
    private static final String NULLABLE_SEASON_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.SeasonPropertyProviders#nullableSeasonProperties";
    private static final String SEASON_PROPERTIES_POPULATED_FROM_CACHE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SeasonPropertyProviders#seasonPropertiesPopulatedFromCache";
    private static final String ENGLISH_AND_CHINESE = "englishAndChinese";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(SEASON_PROPERTIES)
    void fetchesSeasonProperties(
        PropertyGetterFrom<Season> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = euro2024TournamentInfo();
        val seasonId = Urn.parse(tournament.getSeason().getId());

        sapiProperty.setOn(tournament);

        val summaryProvider = SummaryDataProviders.providing(
            in(ENGLISH),
            with(seasonId.toString()),
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

        val sportEvent = (Season) sportEntityFactory.buildSportEvent(seasonId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(NULLABLE_SEASON_PROPERTIES)
    void fetchesSeasonPropertiesAndReturnsNullsForCatchExceptionStrategyWhenTournamentSummaryMissing(
        PropertyGetterFrom<Season> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = euro2024TournamentInfo();
        val season = tournament.getSeason();
        val seasonId = Urn.parse(season.getId());

        sapiProperty.setOn(tournament);

        val summaryProvider = SummaryDataProviders.notProvidingAnyData();

        val dataRouter = new DataRouterImpl();
        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

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
            .build();

        val sportEvent = (Season) sportEntityFactory.buildSportEvent(seasonId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isNull();
    }

    @ParameterizedTest
    @MethodSource(SEASON_PROPERTIES_POPULATED_FROM_CACHE)
    void fetchesSeasonPropertiesAndThrowsForThrowExceptionStrategyWhenTournamentSummaryMissing(
        PropertyGetterFrom<Season> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = euro2024TournamentInfo();
        val season = tournament.getSeason();
        val seasonId = Urn.parse(season.getId());

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

        val sportEvent = (Season) sportEntityFactory.buildSportEvent(seasonId, singletonList(ENGLISH), false);

        assertThatExceptionOfType(ObjectNotFoundException.class)
            .isThrownBy(() -> property.getFrom(sportEvent));
    }

    @ParameterizedTest
    @MethodSource(ENGLISH_AND_CHINESE)
    void fetchesSport(Locale language) throws Exception {
        val tournament = euro2024TournamentInfo();
        val seasonId = Urn.parse(tournament.getSeason().getId());

        val summaryProvider = SummaryDataProviders.providing(
            in(language),
            with(seasonId.toString()),
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

        val seasonSportEvent = (Season) sportEntityFactory.buildSportEvent(
            seasonId,
            singletonList(language),
            false
        );

        val sport = seasonSportEvent.getSport();
        SportSummaryAssert
            .assertThat(sport, in(language))
            .hasIdAndNameEqualTo(getSapiSport(sportIdFrom(tournament), in(language)));

        val sportId = seasonSportEvent.getSportId();
        assertThat(sportId).isEqualTo(sportIdFrom(tournament));
    }

    private SapiCategory categoryFrom(SapiTournamentExtended sapiTournamentExtended) {
        return sapiTournamentExtended.getCategory();
    }

    private Urn sportIdFrom(SapiTournamentInfoEndpoint tournament) {
        return Urn.parse(tournament.getTournament().getSport().getId());
    }

    private SapiSport sportFrom(SapiTournamentExtended tournament) {
        return tournament.getSport();
    }

    @SuppressWarnings("unused")
    static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, CHINESE);
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "MagicNumber" })
class SeasonPropertyProviders {

    private static Stream<Arguments> seasonProperties() {
        return Stream.concat(
            Stream.concat(seasonPropertiesPopulatedFromCache(), smallIntSeasonPropertiesFromCache()),
            alwaysNullTournamentProperties()
        );
    }

    private static Stream<Arguments> nullableSeasonProperties() {
        return Stream.concat(seasonPropertiesPopulatedFromCache(), alwaysNullTournamentProperties());
    }

    private static Stream<Arguments> seasonPropertiesPopulatedFromCache() {
        return Stream.of(
            arguments(
                "scheduledTime - available",
                SportEvent::getScheduledTime,
                p -> {
                    val date = LocalDateTime.of(2025, 3, 4, 2, 3, 4);
                    p.getSeason().setStartDate(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2025, 3, 4, 2, 3, 4).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "scheduledTime - missing",
                SportEvent::getScheduledTime,
                p -> p.getSeason().setStartDate(null),
                null
            ),
            arguments(
                "scheduledEndTime - available",
                SportEvent::getScheduledEndTime,
                p -> {
                    val date = LocalDateTime.of(2024, 5, 6, 7, 8, 9);
                    p.getSeason().setEndDate(XmlGregorianCalendars.forTime(date));
                },
                Date.from(LocalDateTime.of(2024, 5, 6, 7, 8, 9).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "scheduledEndTime - missing",
                SportEvent::getScheduledEndTime,
                p -> p.getSeason().setEndDate(null),
                null
            ),
            arguments(
                "name - available",
                p -> p.getName(ENGLISH),
                p -> p.getSeason().setName("Season Name"),
                "Season Name"
            ),
            arguments("name - empty", p -> p.getName(ENGLISH), p -> p.getSeason().setName(""), ""),
            arguments("name - missing", p -> p.getName(ENGLISH), p -> p.getSeason().setName(null), null)
        );
    }

    private static Stream<Arguments> smallIntSeasonPropertiesFromCache() {
        return Stream.of(
            arguments(
                "coverage - scheduled",
                s -> nullSafeSeasonCoverage(s).getScheduled(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setScheduled(240);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                    p.setSeasonCoverageInfo(coverage);
                },
                240
            ),
            arguments(
                "coverage - played",
                s -> nullSafeSeasonCoverage(s).getPlayed(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setPlayed(120);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                120
            ),
            arguments(
                "coverage - maxCovered - available",
                s -> nullSafeSeasonCoverage(s).getMaxCovered(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCovered(2);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                2
            ),
            arguments(
                "coverage - maxCovered - missing",
                s -> nullSafeSeasonCoverage(s).getMaxCovered(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCovered(null);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                null
            ),
            arguments(
                "coverage - maxCoverageLevel - available",
                s -> nullSafeSeasonCoverage(s).getMaxCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCoverageLevel("gold");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                "gold"
            ),
            arguments(
                "coverage - maxCoverageLevel - missing",
                s -> nullSafeSeasonCoverage(s).getMaxCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCoverageLevel(null);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                null
            ),
            arguments(
                "coverage - maxCoverageLevel - empty",
                s -> nullSafeSeasonCoverage(s).getMaxCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMaxCoverageLevel("");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                ""
            ),
            arguments(
                "coverage - minCoverageLevel - available",
                s -> nullSafeSeasonCoverage(s).getMinCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMinCoverageLevel("gold");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                "gold"
            ),
            arguments(
                "coverage - minCoverageLevel - missing",
                s -> nullSafeSeasonCoverage(s).getMinCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMinCoverageLevel(null);
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                null
            ),
            arguments(
                "coverage - minCoverageLevel - empty",
                s -> nullSafeSeasonCoverage(s).getMinCoverageLevel(),
                p -> {
                    val coverage = fullyPopulatedSeasonCoverageInfo();
                    coverage.setMinCoverageLevel("");
                    p.getTournament().setSeasonCoverageInfo(coverage);
                },
                ""
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

    static Arguments arguments(
        String propertyName,
        PropertyGetterFrom<Season> propertyGetter,
        PropertySetterTo<SapiTournamentInfoEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }

    private static SeasonCoverage nullSafeSeasonCoverage(Season s) {
        return s.getSeasonCoverage() != null ? s.getSeasonCoverage() : new NoopSeasonCoverage();
    }

    private static final class NoopSeasonCoverage implements SeasonCoverage {

        @Override
        public Urn getSeasonId() {
            return null;
        }

        @Override
        public String getMaxCoverageLevel() {
            return null;
        }

        @Override
        public String getMinCoverageLevel() {
            return null;
        }

        @Override
        public Integer getMaxCovered() {
            return null;
        }

        @Override
        public int getPlayed() {
            return 0;
        }

        @Override
        public int getScheduled() {
            return 0;
        }
    }
}
