/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.conn.SapiFixtures;
import com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries;
import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SportEventCacheMatchFixtureTest {

    private static final String MATCH_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.MatchPropertyProviders#matchProperties";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(MATCH_PROPERTIES)
    void retrievesMatchPropertiesFromFetchedFixture(
        PropertyGetterFrom<SportEvent> property,
        PropertySetterTo<SapiFixturesEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val matchFixture = SapiFixtures.soccerMatchGermanyScotlandEuro2024();
        sapiProperty.setOn(matchFixture);
        val matchSummary = SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024();
        val matchId = Urn.parse(matchFixture.getFixture().getId());

        DataRouterImpl dataRouter = new DataRouterImpl();
        val fixtureProvider = FixtureDataProviders.providing(
            in(ENGLISH),
            with(matchId.toString()),
            matchFixture
        );
        val summaryProvider = SummaryDataProviders.providing(
            in(ENGLISH),
            with(matchId.toString()),
            matchSummary
        );
        val tournamentsProvider = TournamentsDataProviders.providing(
            in(ENGLISH),
            tournaments(tournamentEuro2024())
        );
        val sportsProvider = SportsDataProviders.providing(in(ENGLISH), sportsFrom(tournamentEuro2024()));
        val categories = SportsCategoriesDataProviders.providing(
            in(ENGLISH),
            with(sportIdFrom(tournamentEuro2024())),
            categoriesFrom(tournamentEuro2024())
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withFixtures(fixtureProvider)
            .withSummaries(summaryProvider)
            .withTournaments(tournamentsProvider)
            .withSports(sportsProvider)
            .withSportCategories(categories)
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

        val sportEvent = sportEntityFactory.buildSportEvent(matchId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isEqualTo(expected);
    }

    private SapiCategory categoryFrom(SapiTournamentExtended sapiTournamentExtended) {
        return sapiTournamentExtended.getCategory();
    }

    private String sportIdFrom(SapiTournamentExtended tournament) {
        return tournament.getSport().getId();
    }

    private SapiSportCategoriesEndpoint categoriesFrom(SapiTournamentExtended tournament) {
        val categories = new SapiSportCategoriesEndpoint();
        categories.setSport(tournament.getSport());
        categories.setCategories(new SapiCategories());
        categories.getCategories().getCategory().add(tournament.getCategory());
        return categories;
    }

    private SapiSportsEndpoint sportsFrom(SapiTournamentExtended tournament) {
        SapiSportsEndpoint sports = new SapiSportsEndpoint();
        sports.getSport().add(tournament.getSport());
        return sports;
    }

    private SapiSport sportFrom(SapiTournamentExtended tournament) {
        return tournament.getSport();
    }

    private SapiTournamentsEndpoint tournaments(SapiTournamentExtended tournament) {
        val tournaments = new SapiTournamentsEndpoint();
        tournaments.getTournament().add(tournament);
        return tournaments;
    }
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "MagicNumber", "LineLength" })
class MatchPropertyProviders {

    static Stream<Arguments> matchProperties() {
        return Stream.concat(
            Stream.concat(delayedInfoProperties(), bookingStatusProperties()),
            referenceIdsProperties()
        );
    }

    static Stream<Arguments> delayedInfoProperties() {
        return Stream.of(
            arguments(
                "delayedInfo - missing",
                p -> p.getFixture().setDelayedInfo(null),
                Match::getDelayedInfo,
                null
            ),
            arguments(
                "delayedInfo - id",
                p -> {
                    val info = new SapiDelayedInfo();
                    info.setId(123);
                    p.getFixture().setDelayedInfo(info);
                },
                m -> m.getDelayedInfo().getId(),
                123
            ),
            arguments(
                "delayedInfo - description - missing",
                p -> {
                    val info = new SapiDelayedInfo();
                    info.setDescription(null);
                    p.getFixture().setDelayedInfo(info);
                },
                m -> m.getDelayedInfo().getDescription(ENGLISH),
                null
            ),
            arguments(
                "delayedInfo - description - empty",
                p -> {
                    val info = new SapiDelayedInfo();
                    info.setDescription("");
                    p.getFixture().setDelayedInfo(info);
                },
                m -> m.getDelayedInfo().getDescription(ENGLISH),
                ""
            ),
            arguments(
                "delayedInfo - description - available",
                p -> {
                    val info = new SapiDelayedInfo();
                    info.setDescription("delayed description");
                    p.getFixture().setDelayedInfo(info);
                },
                m -> m.getDelayedInfo().getDescription(ENGLISH),
                "delayed description"
            )
        );
    }

    static Stream<Arguments> bookingStatusProperties() {
        return Stream.of(
            arguments(
                "bookingStatus - missing",
                p -> p.getFixture().setLiveodds(null),
                Match::getBookingStatus,
                null
            ),
            arguments(
                "bookingStatus - booked",
                p -> p.getFixture().setLiveodds("booked"),
                Match::getBookingStatus,
                BookingStatus.Booked
            ),
            arguments(
                "bookingStatus - bookable",
                p -> p.getFixture().setLiveodds("bookable"),
                Match::getBookingStatus,
                BookingStatus.Bookable
            ),
            arguments(
                "bookingStatus - buyable",
                p -> p.getFixture().setLiveodds("buyable"),
                Match::getBookingStatus,
                BookingStatus.Buyable
            ),
            arguments(
                "bookingStatus - not_available",
                p -> p.getFixture().setLiveodds("not_available"),
                Match::getBookingStatus,
                BookingStatus.Unavailable
            ),
            arguments(
                "bookingStatus - unknown falls back to unavailable",
                p -> p.getFixture().setLiveodds("not_known_value"),
                Match::getBookingStatus,
                BookingStatus.Unavailable
            )
        );
    }

    @SuppressWarnings("LineLength")
    static Stream<Arguments> referenceIdsProperties() {
        return Stream.concat(
            Stream.concat(lugasReferenceIds(), betradarReferenceIds()),
            betfairReferenceIds()
        );
    }

    static Stream<Arguments> lugasReferenceIds() {
        return Stream.of(
            arguments(
                "referenceIds - lugas ID - collection contains lugas",
                p -> {
                    SapiReferenceIds referenceIds = addReference("lugas", "uuid-like-21844");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("lugas", "uuid-like-21844")
            ),
            arguments(
                "referenceIds - lugas ID - collection contains multiple lugasIDs separated by |",
                p -> {
                    SapiReferenceIds referenceIds = addReference("lugas", "uuid-like-21844|anotherUuid-like");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("lugas", "uuid-like-21844|anotherUuid-like")
            ),
            arguments(
                "referenceIds - lugas ID - collection does not contain lugas",
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("reference", "id")
            ),
            arguments(
                "referenceIds - lugas ID - collection empty",
                p -> p.getFixture().setReferenceIds(new SapiReferenceIds()),
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of()
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns value when collection contains lugas",
                p -> {
                    SapiReferenceIds referenceIds = addReference(
                        "lugas",
                        "43dd49ec-f743-41bd-95d4-314a57779b50"
                    );
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getLugasId(),
                "43dd49ec-f743-41bd-95d4-314a57779b50"
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns null when collection does not contain lugas",
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getLugasId(),
                null
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns null when collection does not contain lugas",
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getLugasId(),
                null
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns null when collection empty",
                p -> p.getFixture().setReferenceIds(new SapiReferenceIds()),
                event -> event.getFixture().getReferences().getLugasId(),
                null
            )
        );
    }

    static Stream<Arguments> betradarReferenceIds() {
        return Stream.of(
            arguments(
                "referenceIds - betradar ID - collection contains BetradarCtrl",
                p -> {
                    SapiReferenceIds referenceIds = addReference("BetradarCtrl", "444");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("BetradarCtrl", "444")
            ),
            arguments(
                "referenceIds - betradar ID - collection contains both betradar and BetradarCtrl both are kept in the map",
                p -> {
                    SapiReferenceIds referenceIds = addReferences("BetradarCtrl", "678", "betradar", "890");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("BetradarCtrl", "678", "betradar", "890")
            ),
            arguments(
                "referenceIds - betradar ID - getBetradarId returns value when collection contains betradar",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betradar", "23");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetradarId(),
                23
            ),
            arguments(
                "referenceIds - betradar ID - getBetradarId returns value when collection contains BetradarCtrl",
                p -> {
                    SapiReferenceIds referenceIds = addReference("BetradarCtrl", "66");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetradarId(),
                66
            ),
            arguments(
                "referenceIds - betradar ID - getBetradarId returns value of betradar when collection contains both betradar and BetradarCtrl",
                p -> {
                    SapiReferenceIds referenceIds = addReferences("BetradarCtrl", "11", "betradar", "55");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetradarId(),
                55
            ),
            arguments(
                "referenceIds - betradar ID - getBetradarId returns null when collection does not contain betradar or BetradarCtrl",
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetradarId(),
                null
            ),
            arguments(
                "referenceIds - betradar ID - getBetradarId returns null when collection contains non-numeric betradar",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betradar", "not-a-number");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetradarId(),
                null
            ),
            arguments(
                "referenceIds - betradar ID - getBetradarId returns null when collection contains non-numeric BetradarCtrl",
                p -> {
                    SapiReferenceIds referenceIds = addReference("BetradarCtrl", "not-a-number");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetradarId(),
                null
            )
        );
    }

    static Stream<Arguments> betfairReferenceIds() {
        return Stream.of(
            arguments(
                "referenceIds - betfair ID - collection contains betfair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betfair", "98");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("betfair", "98")
            ),
            arguments(
                "referenceIds - betfair ID - collection contains betFair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betFair", "88");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("betFair", "88")
            ),
            arguments(
                "referenceIds - betfair ID - collection contains both betFair and betfair and both are kept in the map",
                p -> {
                    SapiReferenceIds referenceIds = addReferences("betFair", "11", "betfair", "22");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getReferences(),
                ImmutableMap.of("betFair", "11", "betfair", "22")
            ),
            arguments(
                "referenceIds - betfair ID - getBetfairId returns value when collection contains betfair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betfair", "66");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetfairId(),
                66
            ),
            arguments(
                "referenceIds - betfair ID - getBetfairId returns value when collection contains betFair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betFair", "77");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetfairId(),
                77
            ),
            arguments(
                "referenceIds - betfair ID - getBetfairId returns value of betfair when collection contains both betfair and betFair",
                p -> {
                    SapiReferenceIds referenceIds = addReferences("betfair", "334", "betFair", "445");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetfairId(),
                334
            ),
            arguments(
                "referenceIds - betfair ID - getBetfairId returns null when collection does not contain betfair or betFair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetfairId(),
                null
            ),
            arguments(
                "referenceIds - betfair ID - getBetfairId returns null when collection contains non-numeric betfair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betfair", "not-a-number");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetfairId(),
                null
            ),
            arguments(
                "referenceIds - betfair ID - getBetfairId returns null when collection contains non-numeric betFair",
                p -> {
                    SapiReferenceIds referenceIds = addReference("betFair", "not-a-number-again");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                event -> event.getFixture().getReferences().getBetfairId(),
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

    private static SapiReferenceIds addReferences(
        String firstName,
        String firstId,
        String secondName,
        String secondId
    ) {
        SapiReferenceIds referenceIds = new SapiReferenceIds();
        SapiReferenceIds.SapiReferenceId referenceId = new SapiReferenceIds.SapiReferenceId();
        referenceId.setName(firstName);
        referenceId.setValue(firstId);
        referenceIds.getReferenceId().add(referenceId);
        SapiReferenceIds.SapiReferenceId secondReferenceId = new SapiReferenceIds.SapiReferenceId();
        secondReferenceId.setName(secondName);
        secondReferenceId.setValue(secondId);
        referenceIds.getReferenceId().add(secondReferenceId);
        return referenceIds;
    }

    static Arguments arguments(
        String propertyName,
        PropertySetterTo<SapiFixturesEndpoint> propertySetterTo,
        PropertyGetterFrom<Match> propertyGetter,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
