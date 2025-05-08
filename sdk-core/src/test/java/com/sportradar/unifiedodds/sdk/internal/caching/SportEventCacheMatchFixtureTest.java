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

    private static final String REFERENCE_IDS_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.MatchPropertyProviders#referenceIdsProperties";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(REFERENCE_IDS_PROPERTIES)
    void fetchesMatchFixtureProperties(
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

@SuppressWarnings({ "unused", "MultipleStringLiterals" })
class MatchPropertyProviders {

    private static Stream<Arguments> referenceIdsProperties() {
        return Stream.of(
            arguments(
                "referenceIds - lugas ID - collection contains lugas",
                event -> event.getFixture().getReferences().getReferences(),
                p -> {
                    SapiReferenceIds referenceIds = addReference("lugas", "uuid-like-21844");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                ImmutableMap.of("lugas", "uuid-like-21844")
            ),
            arguments(
                "referenceIds - lugas ID - collection does not contain lugas",
                event -> event.getFixture().getReferences().getReferences(),
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                ImmutableMap.of("reference", "id")
            ),
            arguments(
                "referenceIds - lugas ID - collection empty",
                event -> event.getFixture().getReferences().getReferences(),
                p -> p.getFixture().setReferenceIds(new SapiReferenceIds()),
                ImmutableMap.of()
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns value when collection contains lugas",
                event -> event.getFixture().getReferences().getLugasId(),
                p -> {
                    SapiReferenceIds referenceIds = addReference(
                        "lugas",
                        "43dd49ec-f743-41bd-95d4-314a57779b50"
                    );
                    p.getFixture().setReferenceIds(referenceIds);
                },
                "43dd49ec-f743-41bd-95d4-314a57779b50"
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns null when collection does not contain lugas",
                event -> event.getFixture().getReferences().getLugasId(),
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                null
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns null when collection does not contain lugas",
                event -> event.getFixture().getReferences().getLugasId(),
                p -> {
                    SapiReferenceIds referenceIds = addReference("reference", "id");
                    p.getFixture().setReferenceIds(referenceIds);
                },
                null
            ),
            arguments(
                "referenceIds - lugas ID - getLugasId returns null when collection empty",
                event -> event.getFixture().getReferences().getLugasId(),
                p -> p.getFixture().setReferenceIds(new SapiReferenceIds()),
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
        PropertyGetterFrom<Match> propertyGetter,
        PropertySetterTo<SapiFixturesEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
