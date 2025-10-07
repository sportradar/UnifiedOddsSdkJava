/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchTimelines.Soccer.FkTosnoGuorKarelia.fkTosnoGuorKareliaMatchTimeline;
import static com.sportradar.unifiedodds.sdk.impl.MatchTimelineDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.utils.Urns.Sports.getForFootball;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SapiEventPlayer;
import com.sportradar.uf.sportsapi.datamodel.SapiEventPlayerAssist;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches;
import com.sportradar.unifiedodds.sdk.entities.EventStatus;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.TimelineEvent;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCaches.BuilderStubbingOutDataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviderImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.SportDataProviders;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.managers.CacheType;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
class SportEventTimelineTest {

    private static final String TIMELINE_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.TimelinePropertiesProvider#timelineProperties";
    private static final DataRouterManager DATA_ROUTER_MANAGER_WITH_NO_PROVIDERS = new DataRouterManagerBuilder()
        .build();
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @ParameterizedTest
    @MethodSource(TIMELINE_PROPERTIES)
    void fetchesMatchTimeline(
        PropertyGetterFrom<TimelineEvent> property,
        PropertySetterTo<SapiMatchTimelineEndpoint> sapiProperty,
        Object expected
    ) {
        val matchTimeline = fkTosnoGuorKareliaMatchTimeline();
        sapiProperty.setOn(matchTimeline);
        val matchId = Urn.parse(matchTimeline.getSportEvent().getId());

        DataRouterImpl dataRouter = new DataRouterImpl();
        val timelineProvider = providing(in(ENGLISH), matchTimeline);

        val dataRouterManager = dataRouterManagerBuilder
            .withMatchTimeline(timelineProvider)
            .with(dataRouter)
            .build();

        dataRouter.setDataListeners(emptyList());

        val sportDataProvider = SportDataProviders
            .stubbingOutSportDataProvider()
            .with(dataRouterManager)
            .withDesiredLocale(ENGLISH)
            .build();

        val timeline = sportDataProvider.getTimelineEvents(matchId, ENGLISH);

        assertThat(property.getFrom(timeline.get(0))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(TIMELINE_PROPERTIES)
    void exportsAndImportsMatchTimelineForFinishedMatch(
        PropertyGetterFrom<TimelineEvent> property,
        PropertySetterTo<SapiMatchTimelineEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val matchTimeline = finished(fkTosnoGuorKareliaMatchTimeline());
        sapiProperty.setOn(matchTimeline);
        val matchId = Urn.parse(matchTimeline.getSportEvent().getId());

        val timelineProvider = providing(in(ENGLISH), matchTimeline);
        DataRouterImpl dataRouter = new DataRouterImpl();
        val dataRouterManager = new DataRouterManagerBuilder()
            .withMatchTimeline(timelineProvider)
            .with(dataRouter)
            .build();

        val sportDataProvider = sportDataProviderFor(dataRouterManager, dataRouter);
        sportDataProvider.getTimelineEvents(matchId, ENGLISH);

        val exported = sportDataProvider.cacheExport(EnumSet.of(CacheType.SportEvent));

        val sportEntityFactoryAfterImport = importing(exported)
            .with(DATA_ROUTER_MANAGER_WITH_NO_PROVIDERS)
            .executes()
            .returningSportEntityFactory();
        val importedMatch = sportEntityFactoryAfterImport.buildSportEvent(
            matchId,
            getForFootball(),
            singletonList(ENGLISH),
            false
        );
        val importedTimeline = ((Match) importedMatch).getEventTimeline(ENGLISH);

        val firstTimelineEvent = importedTimeline.getTimelineEvents().get(0);
        assertThat(property.getFrom(firstTimelineEvent)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource(TIMELINE_PROPERTIES)
    void exportsAndImportsMatchTimelineButIgnoresImportedValuesForLiveMatchAndCallsApi(
        PropertyGetterFrom<TimelineEvent> property,
        PropertySetterTo<SapiMatchTimelineEndpoint> sapiProperty,
        Object expected
    ) throws Exception {
        val matchTimeline = live(fkTosnoGuorKareliaMatchTimeline());
        val matchId = Urn.parse(matchTimeline.getSportEvent().getId());

        val timelineProvider = providing(in(ENGLISH), matchTimeline);
        DataRouterImpl dataRouter = new DataRouterImpl();
        val dataRouterManager = new DataRouterManagerBuilder()
            .withMatchTimeline(timelineProvider)
            .with(dataRouter)
            .build();

        val sportDataProvider = sportDataProviderFor(dataRouterManager, dataRouter);
        sportDataProvider.getTimelineEvents(matchId, ENGLISH);

        val exported = sportDataProvider.cacheExport(EnumSet.of(CacheType.SportEvent));

        val sportEntityFactoryAfterImport = importing(exported)
            .with(dataRouterManager)
            .with(dataRouter)
            .executes()
            .returningSportEntityFactory();

        sapiProperty.setOn(matchTimeline);
        val importedMatch = sportEntityFactoryAfterImport.buildSportEvent(
            matchId,
            getForFootball(),
            singletonList(ENGLISH),
            false
        );
        val importedTimeline = ((Match) importedMatch).getEventTimeline(ENGLISH);

        val firstTimelineEvent = importedTimeline.getTimelineEvents().get(0);
        assertThat(property.getFrom(firstTimelineEvent)).isEqualTo(expected);
    }

    private SportDataProviderImpl sportDataProviderFor(
        DataRouterManager dataRouterManager,
        DataRouterImpl dataRouter
    ) {
        val sportEventCache = stubbingOutDataRouterManager()
            .withDefaultLanguage(ENGLISH)
            .with(dataRouterManager)
            .build();

        dataRouter.setDataListeners(singletonList(sportEventCache));

        val sportEntityFactory = stubbingOutAllCachesAndStatusFactory()
            .withDefaultLanguage(ENGLISH)
            .with(sportEventCache)
            .build();

        return SportDataProviders
            .stubbingOutSportDataProvider()
            .with(dataRouterManager)
            .with(sportEntityFactory)
            .with(sportEventCache)
            .withDesiredLocale(ENGLISH)
            .build();
    }

    private SapiMatchTimelineEndpoint finished(SapiMatchTimelineEndpoint timeline) {
        timeline.getSportEventStatus().setStatus(EventStatus.Finished.getApiName());
        return timeline;
    }

    private SapiMatchTimelineEndpoint live(SapiMatchTimelineEndpoint timeline) {
        timeline.getSportEventStatus().setStatus(EventStatus.Live.getApiName());
        return timeline;
    }

    static ImportConfigurer importing(List<ExportableCi> toImport) {
        assertThat(toImport).isNotEmpty();
        return new ImportConfigurer(toImport);
    }

    @SuppressWarnings({ "HiddenField" })
    static class ImportConfigurer {

        private final List<ExportableCi> toImport;
        private DataRouterManager dataRouterManager;
        private DataRouterImpl dataRouter = new DataRouterImpl();
        private SportEntityFactory sportEntityFactory;

        ImportConfigurer(List<ExportableCi> toImport) {
            this.toImport = toImport;
        }

        ImportConfigurer with(DataRouterManager dataRouterManager) {
            this.dataRouterManager = dataRouterManager;
            return this;
        }

        ImportConfigurer with(DataRouterImpl dataRouter) {
            this.dataRouter = dataRouter;
            return this;
        }

        ImportResult executes() {
            val sportEventCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();

            val sportsDataCache = BuilderStubbingOutDataRouterManager
                .stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();

            val profileCache = ProfileCaches.BuilderStubbingOutDataRouterManager
                .stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();

            sportEntityFactory =
                stubbingOutAllCachesAndStatusFactory()
                    .withDefaultLanguage(ENGLISH)
                    .with(sportEventCache)
                    .with(sportsDataCache)
                    .with(profileCache)
                    .build();

            dataRouter.setDataListeners(ImmutableList.of(sportEventCache, sportsDataCache, profileCache));

            val sportDataProvider = SportDataProviders
                .stubbingOutSportDataProvider()
                .with(dataRouterManager)
                .with(sportEntityFactory)
                .with(sportEventCache)
                .with(sportsDataCache)
                .with(profileCache)
                .withDesiredLocale(ENGLISH)
                .build();

            sportDataProvider.cacheImport(toImport);

            return new ImportResult();
        }

        class ImportResult {

            SportEntityFactory returningSportEntityFactory() {
                return sportEntityFactory;
            }
        }
    }
}

@SuppressWarnings({ "JavaNCSS", "MagicNumber" })
class TimelinePropertiesProvider {

    @SuppressWarnings("unused")
    static Stream<Arguments> timelineProperties() {
        return Stream.of(
            arguments(
                "id - MAX_INT",
                timeline -> timeline.getTimeline().getEvent().get(0).setId(Integer.MAX_VALUE),
                TimelineEvent::getId,
                (long) Integer.MAX_VALUE
            ),
            arguments(
                "id - MAX_LONG",
                timeline -> timeline.getTimeline().getEvent().get(0).setId(Long.MAX_VALUE),
                TimelineEvent::getId,
                Long.MAX_VALUE
            ),
            arguments(
                "type",
                timeline -> timeline.getTimeline().getEvent().get(0).setType("match_started"),
                TimelineEvent::getType,
                "match_started"
            ),
            arguments(
                "matchTime",
                timeline -> timeline.getTimeline().getEvent().get(0).setMatchTime(7),
                TimelineEvent::getMatchTime,
                7
            ),
            arguments(
                "matchClock",
                timeline -> timeline.getTimeline().getEvent().get(0).setMatchClock("6:26"),
                TimelineEvent::getMatchClock,
                "6:26"
            ),
            arguments(
                "team",
                timeline -> timeline.getTimeline().getEvent().get(0).setTeam("home"),
                event -> event.getTeam().name(),
                "Home"
            ),
            arguments(
                "homeScore",
                timeline -> timeline.getTimeline().getEvent().get(0).setHomeScore("4"),
                event -> event.getHomeScore().toString(),
                "4"
            ),
            arguments(
                "awayScore",
                timeline -> timeline.getTimeline().getEvent().get(0).setAwayScore("0"),
                event -> event.getAwayScore().toString(),
                "0"
            ),
            arguments(
                "x",
                timeline -> timeline.getTimeline().getEvent().get(0).setX(95),
                TimelineEvent::getX,
                95
            ),
            arguments(
                "y",
                timeline -> timeline.getTimeline().getEvent().get(0).setY(47),
                TimelineEvent::getY,
                47
            ),
            arguments(
                "periodName",
                timeline -> timeline.getTimeline().getEvent().get(0).setPeriodName("1st half"),
                TimelineEvent::getPeriodName,
                "1st half"
            ),
            arguments(
                "period",
                timeline -> timeline.getTimeline().getEvent().get(0).setPeriod("1"),
                TimelineEvent::getPeriod,
                "1"
            ),
            arguments(
                "matchStatusCode",
                timeline -> timeline.getTimeline().getEvent().get(0).setMatchStatusCode(6),
                TimelineEvent::getMatchStatusCode,
                6
            ),
            arguments(
                "stoppageTime",
                timeline -> timeline.getTimeline().getEvent().get(0).setStoppageTime("2"),
                TimelineEvent::getStoppageTime,
                "2"
            ),
            arguments(
                "value",
                timeline -> timeline.getTimeline().getEvent().get(0).setValue("important"),
                TimelineEvent::getValue,
                "important"
            ),
            arguments(
                "points",
                timeline -> timeline.getTimeline().getEvent().get(0).setPoints("3"),
                TimelineEvent::getPoints,
                "3"
            ),
            arguments(
                "time",
                timeline ->
                    timeline
                        .getTimeline()
                        .getEvent()
                        .get(0)
                        .setTime(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 5, 1, 15, 30, 0))),
                TimelineEvent::getTime,
                Date.from(LocalDateTime.of(2025, 5, 1, 15, 30, 0).toInstant(ZoneOffset.UTC))
            ),
            arguments(
                "goalscorer - null",
                timeline -> timeline.getTimeline().getEvent().get(0).setGoalScorer(null),
                TimelineEvent::getGoalScorer,
                null
            ),
            arguments(
                "goalscorer - name",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:65");
                    goalscorer.setName("John Doe");
                    timeline.getTimeline().getEvent().get(0).setGoalScorer(goalscorer);
                },
                event -> event.getGoalScorer().getName(ENGLISH),
                "John Doe"
            ),
            arguments(
                "goalscorer - name - null",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:32");
                    goalscorer.setName(null);
                    timeline.getTimeline().getEvent().get(0).setGoalScorer(goalscorer);
                },
                event -> event.getGoalScorer().getName(ENGLISH),
                null
            ),
            arguments(
                "goalscorer - names ",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:53");
                    goalscorer.setName("John Bon Jovi");
                    timeline.getTimeline().getEvent().get(0).setGoalScorer(goalscorer);
                },
                event -> event.getGoalScorer().getNames(),
                ImmutableMap.of(ENGLISH, "John Bon Jovi")
            ),
            arguments(
                "goalscorer - names for null name",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:1234");
                    goalscorer.setName(null);
                    timeline.getTimeline().getEvent().get(0).setGoalScorer(goalscorer);
                },
                event -> event.getGoalScorer().getNames().get(ENGLISH),
                null
            ),
            arguments(
                "goalscorer - method - null",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:1234");
                    goalscorer.setMethod(null);
                    timeline.getTimeline().getEvent().get(0).setGoalScorer(goalscorer);
                },
                event -> event.getGoalScorer().getMethod(),
                null
            ),
            arguments(
                "goalscorer - method - available",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:1234");
                    goalscorer.setMethod("penalty");
                    timeline.getTimeline().getEvent().get(0).setGoalScorer(goalscorer);
                },
                event -> event.getGoalScorer().getMethod(),
                "penalty"
            ),
            arguments(
                "player - null",
                timeline -> timeline.getTimeline().getEvent().get(0).setPlayer(null),
                TimelineEvent::getPlayer,
                null
            ),
            arguments(
                "player - name",
                timeline -> {
                    SapiEventPlayer eventPlayer = new SapiEventPlayer();
                    eventPlayer.setId("sr:player:333");
                    eventPlayer.setName("Tick Tack Doe");
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlayer);
                },
                event -> event.getPlayer().getName(ENGLISH),
                "Tick Tack Doe"
            ),
            arguments(
                "player - name - null",
                timeline -> {
                    SapiEventPlayer eventPlater = new SapiEventPlayer();
                    eventPlater.setId("sr:player:35");
                    eventPlater.setName(null);
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlater);
                },
                event -> event.getPlayer().getName(ENGLISH),
                null
            ),
            arguments(
                "player - name - empty",
                timeline -> {
                    SapiEventPlayer eventPlater = new SapiEventPlayer();
                    eventPlater.setId("sr:player:35");
                    eventPlater.setName("");
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlater);
                },
                event -> event.getPlayer().getName(ENGLISH),
                ""
            ),
            arguments(
                "player - names ",
                timeline -> {
                    SapiEventPlayer goalscorer = new SapiEventPlayer();
                    goalscorer.setId("sr:player:123");
                    goalscorer.setName("Jake Bon Jovi");
                    timeline.getTimeline().getEvent().get(0).setPlayer(goalscorer);
                },
                event -> event.getPlayer().getNames(),
                ImmutableMap.of(ENGLISH, "Jake Bon Jovi")
            ),
            arguments(
                "player - names for null name",
                timeline -> {
                    SapiEventPlayer eventPlayer = new SapiEventPlayer();
                    eventPlayer.setId("sr:player:6663");
                    eventPlayer.setBench(null);
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlayer);
                },
                event -> event.getPlayer().getNames().get(ENGLISH),
                null
            ),
            arguments(
                "player - bench - null",
                timeline -> {
                    SapiEventPlayer eventPlayer = new SapiEventPlayer();
                    eventPlayer.setId("sr:player:5534");
                    eventPlayer.setBench(null);
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlayer);
                },
                event -> event.getPlayer().getBench(),
                null
            ),
            arguments(
                "player - bench - empty",
                timeline -> {
                    SapiEventPlayer eventPlayer = new SapiEventPlayer();
                    eventPlayer.setId("sr:player:5534");
                    eventPlayer.setBench("");
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlayer);
                },
                event -> event.getPlayer().getBench(),
                ""
            ),
            arguments(
                "goalscorer - bench - available",
                timeline -> {
                    SapiEventPlayer eventPlayer = new SapiEventPlayer();
                    eventPlayer.setId("sr:player:8855");
                    eventPlayer.setBench("yes");
                    timeline.getTimeline().getEvent().get(0).setPlayer(eventPlayer);
                },
                event -> event.getPlayer().getBench(),
                "yes"
            ),
            arguments("toString", timeline -> {}, event -> event.toString().isEmpty(), false),
            arguments(
                "toString - assists empty",
                timeline -> timeline.getTimeline().getEvent().get(0).getAssist().clear(),
                event -> event.toString().isEmpty(),
                false
            ),
            arguments(
                "assists - null for empty",
                timeline -> timeline.getTimeline().getEvent().get(0).getAssist().clear(),
                TimelineEvent::getAssists,
                null
            ),
            arguments(
                "assists - name - null",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:1111");
                    assist.setName(null);
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getName(ENGLISH),
                null
            ),
            arguments(
                "assists - name - empty",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:133");
                    assist.setName("");
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getName(ENGLISH),
                ""
            ),
            arguments(
                "assists - name - available",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:12");
                    assist.setName("Alfred Doe");
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getName(ENGLISH),
                "Alfred Doe"
            ),
            arguments(
                "assists - names - null name",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:1111");
                    assist.setName(null);
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getNames().get(ENGLISH),
                null
            ),
            arguments(
                "assists - names - empty name",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:11141");
                    assist.setName("");
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getNames(),
                ImmutableMap.of(ENGLISH, "")
            ),
            arguments(
                "assists - names - name available",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:2569");
                    assist.setName("Carl Martin");
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getNames(),
                ImmutableMap.of(ENGLISH, "Carl Martin")
            ),
            arguments(
                "assists - type - null",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:990");
                    assist.setType(null);
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getType(),
                null
            ),
            arguments(
                "assists - type - empty",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:990");
                    assist.setType("");
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getType(),
                ""
            ),
            arguments(
                "assists - type - available",
                timeline -> {
                    SapiEventPlayerAssist assist = new SapiEventPlayerAssist();
                    assist.setId("sr:player:990");
                    assist.setType("pass");
                    timeline.getTimeline().getEvent().get(0).getAssist().add(assist);
                },
                event -> event.getAssists().get(0).getType(),
                "pass"
            )
        );
    }

    static Arguments arguments(
        String propertyName,
        PropertySetterTo<SapiMatchTimelineEndpoint> propertySetterTo,
        PropertyGetterFrom<TimelineEvent> propertyGetter,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
