/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.SapiCategories.getSapiCategory;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SimpleTournaments.ClubFriendlyGames.clubFriendlyGames;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.BasicTournament;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "ClassFanOutComplexity", "LineLength" })
class SportEventCacheBasicTournamentTest {

    private static final String ENGLISH_AND_CHINESE =
        "com.sportradar.unifiedodds.sdk.internal.caching.SportEventCacheBasicTournamentTest#englishAndChinese";
    private static final String TOURNAMENT_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.BasicTournamentPropertyProviders#tournamentProperties";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROPERTIES)
    void fetchesTournamentProperties(
        PropertyGetterFrom<BasicTournament> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = clubFriendlyGames(ENGLISH);
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

        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        val sportEvent = (BasicTournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(ENGLISH),
            false
        );

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(TOURNAMENT_PROPERTIES)
    void fetchesTournamentPropertiesAndReturnsNullsForCatchExceptionStrategyWhenTournamentSummaryMissing(
        PropertyGetterFrom<BasicTournament> property,
        PropertySetterTo<SapiTournamentInfoEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val tournament = clubFriendlyGames(ENGLISH);
        val tournamentId = Urn.parse(tournament.getTournament().getId());

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

        val sportEvent = (BasicTournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(ENGLISH),
            false
        );

        assertThat(property.getFrom(sportEvent)).isNull();
    }

    @ParameterizedTest
    @MethodSource(ENGLISH_AND_CHINESE)
    void fetchesSport(Locale language) throws Exception {
        val tournament = clubFriendlyGames(language);
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
            .providing(in(language), categoryFrom(tournament))
            .providing(in(language), sportFrom(tournament));

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

        val tournamentSportEvent = (BasicTournament) sportEntityFactory.buildSportEvent(
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
    @MethodSource(ENGLISH_AND_CHINESE)
    void fetchesCategory(Locale language) throws Exception {
        val tournament = clubFriendlyGames(language);
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

        val sportsDataCache = SportsDataCaches.providing(in(language), categoryFrom(tournament));

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

        val tournamentSportEvent = (BasicTournament) sportEntityFactory.buildSportEvent(
            tournamentId,
            singletonList(language),
            false
        );

        val category = tournamentSportEvent.getCategory();

        CategorySummaryAssert
            .assertThat(category, in(language))
            .hasIdNameAncCountryCodeEqualTo(
                getSapiCategory(categoryIdFrom(tournament), in(language)).getCategory()
            );
    }

    private Urn categoryIdFrom(SapiTournamentInfoEndpoint tournament) {
        return Urn.parse(tournament.getTournament().getCategory().getId());
    }

    private Urn sportIdFrom(SapiTournamentInfoEndpoint tournament) {
        return Urn.parse(tournament.getTournament().getSport().getId());
    }

    private SapiCategory categoryFrom(SapiTournamentInfoEndpoint tournament) {
        return tournament.getTournament().getCategory();
    }

    private SapiSport sportFrom(SapiTournamentInfoEndpoint tournament) {
        return tournament.getTournament().getSport();
    }

    @SuppressWarnings("unused")
    static Stream<Locale> englishAndChinese() {
        return Stream.of(ENGLISH, CHINESE);
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "MagicNumber" })
class BasicTournamentPropertyProviders {

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
                "scheduledTime - null when missing in both tournament and tournamentLength",
                SportEvent::getScheduledTime,
                p -> {
                    p.getTournament().setScheduled(null);
                    p.getTournament().setTournamentLength(null);
                },
                null
            ),
            arguments(
                "scheduledTime - taken from tournamentLength when missing under tournament",
                SportEvent::getScheduledTime,
                p -> {
                    p.getTournament().setScheduled(null);
                    val startDate = LocalDateTime.of(2025, 2, 3, 2, 3, 4);
                    val length = new SapiTournamentLength();
                    length.setStartDate(XmlGregorianCalendars.forTime(startDate));
                    p.getTournament().setTournamentLength(length);
                },
                Date.from(LocalDateTime.of(2025, 2, 3, 2, 3, 4).toInstant(ZoneOffset.UTC))
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
                "scheduledEndTime - null when missing from both tournament and tournamentLength",
                SportEvent::getScheduledEndTime,
                p -> {
                    p.getTournament().setScheduledEnd(null);
                    p.getTournament().setTournamentLength(null);
                },
                null
            ),
            arguments(
                "scheduledEndTime - taken from tournamentLength when missing under tournament",
                SportEvent::getScheduledEndTime,
                p -> {
                    p.getTournament().setScheduledEnd(null);
                    val endDate = LocalDateTime.of(2025, 2, 3, 2, 3, 4);
                    val length = new SapiTournamentLength();
                    length.setEndDate(XmlGregorianCalendars.forTime(endDate));
                    p.getTournament().setTournamentLength(length);
                },
                Date.from(LocalDateTime.of(2025, 2, 3, 2, 3, 4).toInstant(ZoneOffset.UTC))
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
            ),
            arguments(
                "isExhibitionGames - true",
                BasicTournament::isExhibitionGames,
                p -> p.getTournament().setExhibitionGames(true),
                true
            ),
            arguments(
                "isExhibitionGames - false",
                BasicTournament::isExhibitionGames,
                p -> p.getTournament().setExhibitionGames(false),
                false
            ),
            arguments(
                "isExhibitionGames - null",
                BasicTournament::isExhibitionGames,
                p -> p.getTournament().setExhibitionGames(null),
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
        PropertyGetterFrom<BasicTournament> propertyGetter,
        PropertySetterTo<SapiTournamentInfoEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
