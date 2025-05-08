/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiPitchers.joseSuarez;
import static com.sportradar.unifiedodds.sdk.conn.SapiPitchers.yuseiKikuchi;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "MagicNumber", "LineLength" })
class SportEventCacheMatchSummaryTest {

    private static final String MATCH_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.caching.MatchSummaryPropertyProviders#matchProperties";
    private static final String MATCH_PROPERTIES_WHEN_SUMMARY_MISSING =
        "com.sportradar.unifiedodds.sdk.internal.caching.MatchSummaryPropertyProviders#matchPropertiesWhenSummaryMissing";
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(MATCH_PROPERTIES)
    void fetchesMatchSummaryProperties(
        PropertyGetterFrom<SportEvent> property,
        PropertySetterTo<SapiMatchSummaryEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val matchSummary = SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024();
        sapiProperty.setOn(matchSummary);
        val matchId = Urn.parse(matchSummary.getSportEvent().getId());

        DataRouterImpl dataRouter = new DataRouterImpl();
        val summaryProvider = SummaryDataProviders.providing(
            in(ENGLISH),
            with(matchId.toString()),
            matchSummary
        );
        val sportsProvider = SportsDataProviders.providing(in(ENGLISH), sportsFrom(tournamentEuro2024()));
        val categories = SportsCategoriesDataProviders.providing(
            in(ENGLISH),
            with(sportIdFrom(tournamentEuro2024())),
            categoriesFrom(tournamentEuro2024())
        );

        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
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

    @Test
    void fetchingMatchThrowsExceptionWhenSummaryMissingAndErrorStrategyIsThrow() throws Exception {
        val matchSummary = SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024();
        val matchId = Urn.parse(matchSummary.getSportEvent().getId());

        val dataRouter = new DataRouterImpl();
        val summaryProvider = SummaryDataProviders.notProvidingAnyData();

        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .with(dataRouter)
            .build();

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
            .build();

        assertThatExceptionOfType(ObjectNotFoundException.class)
            .isThrownBy(() -> sportEntityFactory.buildSportEvent(matchId, singletonList(ENGLISH), false));
    }

    @ParameterizedTest
    @MethodSource(MATCH_PROPERTIES_WHEN_SUMMARY_MISSING)
    void fetchingMatchPropertiesWithStrategyCatchReturnsNullForEachPropertyWhenSummaryFixtureAndTimelineMissing(
        PropertyGetterFrom<SportEvent> property
    ) throws Exception {
        val matchSummary = SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024();
        val matchId = Urn.parse(matchSummary.getSportEvent().getId());

        val dataRouter = new DataRouterImpl();
        val summaryProvider = SummaryDataProviders.notProvidingAnyData();
        val fixtureProvider = FixtureDataProviders.notProvidingAnyData();
        val matchTimelineProvider = MatchTimelineDataProviders.notProvidingAnyData();

        val dataRouterManager = dataRouterManagerBuilder
            .withSummaries(summaryProvider)
            .withFixtures(fixtureProvider)
            .withMatchTimeline(matchTimelineProvider)
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

        val sportEvent = sportEntityFactory.buildSportEvent(matchId, singletonList(ENGLISH), false);

        assertThat(property.getFrom(sportEvent)).isNull();
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
}

@SuppressWarnings({ "unused", "MultipleStringLiterals", "JavaNCSS", "MagicNumber" })
class MatchSummaryPropertyProviders {

    private static Stream<Arguments> matchProperties() {
        return Stream.concat(
            venueProperties(),
            Stream.concat(conditionsProperties(), tournamentProperties())
        );
    }

    private static Stream<Arguments> venueProperties() {
        return Stream.of(
            arguments(
                "venue - id",
                event -> event.getVenue().getId(),
                p -> p.getSportEvent().getVenue().setId("sr:venue:11"),
                "sr:venue:11"
            ),
            arguments(
                "venue - name",
                event -> event.getVenue().getName(ENGLISH),
                p -> p.getSportEvent().getVenue().setName("Allianz Arena"),
                "Allianz Arena"
            ),
            arguments(
                "venue - name - empty",
                event -> event.getVenue().getName(ENGLISH),
                p -> p.getSportEvent().getVenue().setName(""),
                ""
            ),
            arguments(
                "venue - name - missing",
                event -> event.getVenue().getName(ENGLISH),
                p -> p.getSportEvent().getVenue().setName(null),
                null
            ),
            arguments(
                "venue - names - available",
                event -> event.getVenue().getNames(),
                p -> p.getSportEvent().getVenue().setName("Allianz Arena"),
                ImmutableMap.of(ENGLISH, "Allianz Arena")
            ),
            arguments(
                "venue - names - missing",
                event -> event.getVenue().getNames(),
                p -> p.getSportEvent().getVenue().setName(null),
                ImmutableMap.of()
            ),
            arguments(
                "venue - names - empty",
                event -> event.getVenue().getNames(),
                p -> p.getSportEvent().getVenue().setName(""),
                ImmutableMap.of(ENGLISH, "")
            ),
            arguments(
                "venue - city - available",
                event -> event.getVenue().getCity(ENGLISH),
                p -> p.getSportEvent().getVenue().setCityName("Amsterdam"),
                "Amsterdam"
            ),
            arguments(
                "venue - city - missing",
                event -> event.getVenue().getCity(ENGLISH),
                p -> p.getSportEvent().getVenue().setCityName(null),
                null
            ),
            arguments(
                "venue - city - empty",
                event -> event.getVenue().getCity(ENGLISH),
                p -> p.getSportEvent().getVenue().setCityName(""),
                ""
            ),
            arguments(
                "venue - cities - available",
                event -> event.getVenue().getCities(),
                p -> p.getSportEvent().getVenue().setCityName("Amsterdam"),
                ImmutableMap.of(ENGLISH, "Amsterdam")
            ),
            arguments(
                "venue - cities - missing",
                event -> event.getVenue().getCities(),
                p -> p.getSportEvent().getVenue().setCityName(null),
                ImmutableMap.of()
            ),
            arguments(
                "venue - cities - empty",
                event -> event.getVenue().getCities(),
                p -> p.getSportEvent().getVenue().setCityName(""),
                ImmutableMap.of(ENGLISH, "")
            ),
            arguments(
                "venue - coordinates - available",
                event -> event.getVenue().getCoordinates(),
                p -> p.getSportEvent().getVenue().setMapCoordinates("19.075358,-98.164900"),
                "19.075358,-98.164900"
            ),
            arguments(
                "venue - coordinates - empty",
                event -> event.getVenue().getCoordinates(),
                p -> p.getSportEvent().getVenue().setMapCoordinates(""),
                ""
            ),
            arguments(
                "venue - coordinates - missing",
                event -> event.getVenue().getCoordinates(),
                p -> p.getSportEvent().getVenue().setMapCoordinates(null),
                null
            ),
            arguments(
                "venue - countryCode - available",
                event -> event.getVenue().getCountryCode(),
                p -> p.getSportEvent().getVenue().setCountryCode("MEX"),
                "MEX"
            ),
            arguments(
                "venue - countryCode - empty",
                event -> event.getVenue().getCountryCode(),
                p -> p.getSportEvent().getVenue().setCountryCode(""),
                ""
            ),
            arguments(
                "venue - countryCode - missing",
                event -> event.getVenue().getCountryCode(),
                p -> p.getSportEvent().getVenue().setCountryCode(null),
                null
            ),
            arguments(
                "venue - capacity - available",
                event -> event.getVenue().getCapacity(),
                p -> p.getSportEvent().getVenue().setCapacity(12112),
                12112
            ),
            arguments(
                "venue - capacity - missing",
                event -> event.getVenue().getCapacity(),
                p -> p.getSportEvent().getVenue().setCapacity(null),
                null
            ),
            arguments(
                "venue - country - available",
                event -> event.getVenue().getCountry(ENGLISH),
                p -> p.getSportEvent().getVenue().setCountryName("Germany"),
                "Germany"
            ),
            arguments(
                "venue - country - empty",
                event -> event.getVenue().getCountry(ENGLISH),
                p -> p.getSportEvent().getVenue().setCountryName(""),
                ""
            ),
            arguments(
                "venue - country - missing",
                event -> event.getVenue().getCountry(ENGLISH),
                p -> p.getSportEvent().getVenue().setCountryName(null),
                null
            ),
            arguments(
                "venue - countries - available",
                event -> event.getVenue().getCountries(),
                p -> p.getSportEvent().getVenue().setCountryName("Poland"),
                ImmutableMap.of(ENGLISH, "Poland")
            ),
            arguments(
                "venue - countries - empty",
                event -> event.getVenue().getCountries(),
                p -> p.getSportEvent().getVenue().setCountryName(""),
                ImmutableMap.of(ENGLISH, "")
            ),
            arguments(
                "venue - countries - missing",
                event -> event.getVenue().getCountries(),
                p -> p.getSportEvent().getVenue().setCountryName(null),
                ImmutableMap.of()
            ),
            arguments(
                "venue - state - available",
                event -> event.getVenue().getState(),
                p -> p.getSportEvent().getVenue().setState("CA"),
                "CA"
            ),
            arguments(
                "venue - state - empty",
                event -> event.getVenue().getState(),
                p -> p.getSportEvent().getVenue().setState(""),
                ""
            ),
            arguments(
                "venue - state - missing",
                event -> event.getVenue().getState(),
                p -> p.getSportEvent().getVenue().setState(null),
                null
            ),
            arguments(
                "venue - toString",
                event -> event.getVenue().toString().contains("Venue"),
                p -> {},
                true
            )
        );
    }

    private static Stream<Arguments> conditionsProperties() {
        return Stream.of(
            arguments(
                "conditions - missing",
                Competition::getConditions,
                p -> p.setSportEventConditions(null),
                null
            ),
            arguments(
                "conditions - match mode - available",
                event -> event.getConditions().getEventMode(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setMatchMode("mode-1");
                    p.setSportEventConditions(conditions);
                },
                "mode-1"
            ),
            arguments(
                "conditions - match mode - missing",
                event -> event.getConditions().getEventMode(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setMatchMode(null);
                    p.setSportEventConditions(conditions);
                },
                null
            ),
            arguments(
                "conditions - match mode - empty",
                event -> event.getConditions().getEventMode(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setMatchMode("");
                    p.setSportEventConditions(conditions);
                },
                ""
            ),
            arguments(
                "conditions - attendance - available",
                event -> event.getConditions().getAttendance(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setAttendance("65051");
                    p.setSportEventConditions(conditions);
                },
                "65051"
            ),
            arguments(
                "conditions - attendance - missing",
                event -> event.getConditions().getAttendance(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setAttendance(null);
                    p.setSportEventConditions(conditions);
                },
                null
            ),
            arguments(
                "conditions - attendance - empty",
                event -> event.getConditions().getAttendance(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setAttendance("");
                    p.setSportEventConditions(conditions);
                },
                ""
            ),
            arguments(
                "conditions - referee.id available",
                event -> event.getConditions().getReferee().getId(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    val referee = new SapiReferee();
                    referee.setId("sr:referee:11");
                    conditions.setReferee(referee);
                    p.setSportEventConditions(conditions);
                },
                Urn.parse("sr:referee:11")
            ),
            arguments(
                "conditions - referee.name available",
                event -> event.getConditions().getReferee().getName(),
                p -> p.getSportEventConditions().getReferee().setName("Turpin, Clem"),
                "Turpin, Clem"
            ),
            arguments(
                "conditions - referee.name empty",
                event -> event.getConditions().getReferee().getName(),
                p -> p.getSportEventConditions().getReferee().setName(""),
                ""
            ),
            arguments(
                "conditions - referee.name missing",
                event -> event.getConditions().getReferee().getName(),
                p -> p.getSportEventConditions().getReferee().setName(null),
                null
            ),
            arguments(
                "conditions - referee.nationality available",
                event -> event.getConditions().getReferee().getNationality(ENGLISH),
                p -> p.getSportEventConditions().getReferee().setNationality("France"),
                "France"
            ),
            arguments(
                "conditions - referee.nationalities available",
                event -> event.getConditions().getReferee().getNationalities(),
                p -> p.getSportEventConditions().getReferee().setNationality("France"),
                ImmutableMap.of(ENGLISH, "France")
            ),
            arguments(
                "conditions - referee.nationalities empty",
                event -> event.getConditions().getReferee().getNationalities(),
                p -> p.getSportEventConditions().getReferee().setNationality(null),
                ImmutableMap.of()
            ),
            arguments(
                "conditions - referee missing",
                event -> event.getConditions().getReferee(),
                p -> {
                    val conditions = new SapiSportEventConditions();
                    conditions.setReferee(null);
                    p.setSportEventConditions(conditions);
                },
                null
            ),
            arguments(
                "conditions - weatherInfo missing",
                event -> event.getConditions().getWeatherInfo(),
                p -> p.getSportEventConditions().setWeatherInfo(null),
                null
            ),
            arguments(
                "conditions - weatherInfo.weatherConditions available",
                event -> event.getConditions().getWeatherInfo().getWeatherConditions(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWeatherConditions("good");
                },
                "good"
            ),
            arguments(
                "conditions - weatherInfo.weatherConditions missing",
                event -> event.getConditions().getWeatherInfo().getWeatherConditions(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWeatherConditions(null);
                },
                null
            ),
            arguments(
                "conditions - weatherInfo.weatherConditions empty",
                event -> event.getConditions().getWeatherInfo().getWeatherConditions(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWeatherConditions("");
                },
                ""
            ),
            arguments(
                "conditions - weatherInfo.pitch available",
                event -> event.getConditions().getWeatherInfo().getPitch(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setPitch("bad");
                },
                "bad"
            ),
            arguments(
                "conditions - weatherInfo.pitch missing",
                event -> event.getConditions().getWeatherInfo().getPitch(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setPitch(null);
                },
                null
            ),
            arguments(
                "conditions - weatherInfo.pitch empty",
                event -> event.getConditions().getWeatherInfo().getPitch(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setPitch("");
                },
                ""
            ),
            arguments(
                "conditions - weatherInfo.windAdvantage available",
                event -> event.getConditions().getWeatherInfo().getWindAdvantage(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWindAdvantage("none");
                },
                "none"
            ),
            arguments(
                "conditions - weatherInfo.windAdvantage missing",
                event -> event.getConditions().getWeatherInfo().getWindAdvantage(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWindAdvantage(null);
                },
                null
            ),
            arguments(
                "conditions - weatherInfo.windAdvantage empty",
                event -> event.getConditions().getWeatherInfo().getWindAdvantage(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWindAdvantage("");
                },
                ""
            ),
            arguments(
                "conditions - weatherInfo.wind available",
                event -> event.getConditions().getWeatherInfo().getWind(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWind("mild");
                },
                "mild"
            ),
            arguments(
                "conditions - weatherInfo.wind missing",
                event -> event.getConditions().getWeatherInfo().getWind(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWind(null);
                },
                null
            ),
            arguments(
                "conditions - weatherInfo.wind empty",
                event -> event.getConditions().getWeatherInfo().getWind(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setWind("");
                },
                ""
            ),
            arguments(
                "conditions - weatherInfo.temperature available",
                event -> event.getConditions().getWeatherInfo().getTemperature(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setTemperatureCelsius(21);
                },
                21
            ),
            arguments(
                "conditions - weatherInfo.temperature missing",
                event -> event.getConditions().getWeatherInfo().getTemperature(),
                p -> {
                    p.getSportEventConditions().setWeatherInfo(new SapiWeatherInfo());
                    p.getSportEventConditions().getWeatherInfo().setTemperatureCelsius(null);
                },
                null
            ),
            arguments(
                "conditions - pitchers missing",
                event -> event.getConditions().getPitchers(),
                p -> p.getSportEventConditions().setPitchers(null),
                null
            ),
            arguments(
                "conditions - pitchers empty",
                event -> event.getConditions().getPitchers(),
                p -> p.getSportEventConditions().setPitchers(new SapiPitchers()),
                null
            ),
            arguments(
                "conditions - pitchers available",
                event -> event.getConditions().getPitchers().size(),
                p -> {
                    SapiPitchers pitchers = new SapiPitchers();
                    pitchers.getPitcher().add(yuseiKikuchi());
                    pitchers.getPitcher().add(joseSuarez());
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                2
            ),
            arguments(
                "conditions - pitcher.name available",
                event -> event.getConditions().getPitchers().get(0).getName(),
                p -> {
                    SapiPitchers pitchers = new SapiPitchers();
                    pitchers.getPitcher().add(yuseiKikuchi());
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                yuseiKikuchi().getName()
            ),
            arguments(
                "conditions - pitcher.name empty",
                event -> event.getConditions().getPitchers().get(0).getName(),
                p -> {
                    SapiPitchers pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setName("");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                ""
            ),
            arguments(
                "conditions - pitcher.name missing",
                event -> event.getConditions().getPitchers().get(0).getName(),
                p -> {
                    SapiPitchers pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setName(null);
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                null
            ),
            arguments(
                "conditions - pitcher.hand L and translated",
                event -> event.getConditions().getPitchers().get(0).getHand(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setHand("L");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                PitcherHand.Left
            ),
            arguments(
                "conditions - pitcher.hand 'l' and translated",
                event -> event.getConditions().getPitchers().get(0).getHand(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setHand("l");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                PitcherHand.Left
            ),
            arguments(
                "conditions - pitcher.hand R and translated",
                event -> event.getConditions().getPitchers().get(0).getHand(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setHand("R");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                PitcherHand.Right
            ),
            arguments(
                "conditions - pitcher.hand 'r' and translated",
                event -> event.getConditions().getPitchers().get(0).getHand(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setHand("r");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                PitcherHand.Right
            ),
            arguments(
                "conditions - pitcher.hand missing",
                event -> event.getConditions().getPitchers().get(0).getHand(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setHand(null);
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                null
            ),
            arguments(
                "conditions - pitcher.hand empty",
                event -> event.getConditions().getPitchers().get(0).getHand(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setHand("");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                null
            ),
            arguments(
                "conditions - pitcher.competitor home",
                event -> event.getConditions().getPitchers().get(0).getCompetitor(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setCompetitor("home");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                HomeAway.Home
            ),
            arguments(
                "conditions - pitcher.competitor away",
                event -> event.getConditions().getPitchers().get(0).getCompetitor(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setCompetitor("away");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                HomeAway.Away
            ),
            arguments(
                "conditions - pitcher.competitor unsupported",
                event -> event.getConditions().getPitchers().get(0).getCompetitor(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setCompetitor("not-known");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                null
            ),
            arguments(
                "conditions - pitcher.competitor empty",
                event -> event.getConditions().getPitchers().get(0).getCompetitor(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setCompetitor("");
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                null
            ),
            arguments(
                "conditions - pitcher.competitor missing",
                event -> event.getConditions().getPitchers().get(0).getCompetitor(),
                p -> {
                    val pitchers = new SapiPitchers();
                    val pitcher = yuseiKikuchi();
                    pitcher.setCompetitor(null);
                    pitchers.getPitcher().add(pitcher);
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                null
            ),
            arguments(
                "conditions - pitcher.id available",
                event -> event.getConditions().getPitchers().get(0).getId(),
                p -> {
                    val pitchers = new SapiPitchers();
                    pitchers.getPitcher().add(yuseiKikuchi());
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                Urn.parse(yuseiKikuchi().getId())
            ),
            arguments(
                "conditions - toString includes pitcher's id",
                event -> event.getConditions().toString().contains(yuseiKikuchi().getId()),
                p -> {
                    val pitchers = new SapiPitchers();
                    pitchers.getPitcher().add(yuseiKikuchi());
                    p.getSportEventConditions().setPitchers(pitchers);
                },
                true
            ),
            arguments(
                "conditions - toString works without pitchers",
                event -> event.getConditions().toString().contains("SportEventConditions"),
                p -> p.getSportEventConditions().setPitchers(null),
                true
            )
        );
    }

    private static Stream<Arguments> tournamentProperties() {
        return Stream.of(
            arguments(
                "tournament - id",
                event -> event.getTournament().getId(),
                p -> p.getSportEvent().getTournament().setId("sr:tournament:11"),
                Urn.parse("sr:tournament:11")
            ),
            arguments(
                "tournament - name",
                event -> event.getTournament().getName(ENGLISH),
                p -> p.getSportEvent().getTournament().setName("Euro 2030"),
                "Euro 2030"
            )
        );
    }

    private static Stream<Arguments> matchPropertiesWhenSummaryMissing() {
        return Stream.of(
            arguments("sportId", Match::getSportId),
            arguments("sport", Match::getSport),
            arguments("status", Match::getStatus),
            arguments("bookingStatus", Match::getBookingStatus),
            arguments("venue", Match::getVenue),
            arguments("conditions", Match::getConditions),
            arguments("competitors", Match::getCompetitors),
            arguments("season", Match::getSeason),
            arguments("homeCompetitor", Match::getHomeCompetitor),
            arguments("awayCompetitor", Match::getAwayCompetitor),
            arguments("tournament", Match::getTournament),
            arguments("name", m -> m.getName(ENGLISH)),
            arguments("scheduledTime", Match::getScheduledTime),
            arguments("scheduledEndTime", Match::getScheduledEndTime),
            arguments("isStartTimeTbd", Match::isStartTimeTbd),
            arguments("replacedBy", Match::getReplacedBy),
            arguments("fixture", Match::getFixture),
            arguments("eventTimeline", m -> m.getEventTimeline(ENGLISH)),
            arguments("delayedInfo", Match::getDelayedInfo),
            arguments("coverageInfo", Match::getCoverageInfo),
            arguments("liveOdds", Match::getLiveOdds),
            arguments("sportEventType", Match::getSportEventType)
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
        PropertySetterTo<SapiMatchSummaryEndpoint> propertySetterTo,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }

    static Arguments arguments(String propertyName, PropertyGetterFrom<Match> propertyGetter) {
        return Arguments.of(Named.of(propertyName, propertyGetter));
    }
}
