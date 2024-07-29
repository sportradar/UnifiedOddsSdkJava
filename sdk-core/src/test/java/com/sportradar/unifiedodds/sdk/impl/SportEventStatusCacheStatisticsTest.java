/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEventStatusCaches.BuilderStubbingOutSportEventCache.stubbingOutSportEventCache;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEventStatusFactories.BuilderStubbingOutStatusValueCache.stubbingOutStatusValueCacheWith;
import static com.sportradar.unifiedodds.sdk.caching.impl.StatusCachePopulator.populate;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.*;
import static com.sportradar.unifiedodds.sdk.conn.StatisticsAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.conn.UfSportEventStatuses.*;
import static com.sportradar.unifiedodds.sdk.conn.UfSportEventStatuses.kabaddiMatchFeedStatus;
import static com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.via;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.Urn.parse;
import static com.sportradar.utils.Urns.SportEvents.getForAnyMatch;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.datamodel.UfSportEventStatus;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.SportEventCaches;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventStatusCacheImpl;
import com.sportradar.unifiedodds.sdk.conn.StatisticsAssert;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SportEventStatusCacheStatisticsTest {

    @Nested
    public class PopulatedFromMessage {

        private Urn matchUrn = getForAnyMatch();
        private final boolean noRefreshViaApi = false;

        @Test
        public void cachesNoStatisticsForMessageCarryingNoStatistics() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();
            UfSportEventStatus ufStatus = kabaddiMatchFeedStatus();
            ufStatus.setStatistics(null);

            SportEventStatusDto statusDto = new SportEventStatusDto(ufStatus);
            populate(statusCache).fromMessage(with(matchUrn), statusDto);

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                noRefreshViaApi
            );

            Assertions.assertThat(matchStatus.getStatistics()).isNull();
        }

        @Test
        public void cachesKabaddiTotalStatistics() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();
            UfSportEventStatus ufStatus = withEveryStatistic(kabaddiMatchFeedStatus());

            SportEventStatusDto statusDto = new SportEventStatusDto(ufStatus);
            populate(statusCache).fromMessage(with(matchUrn), statusDto);

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                noRefreshViaApi
            );

            val statistics = matchStatus.getStatistics();

            assertThat(statistics).totalsEqualToThoseIn(ufStatus);
            assertThat(statistics.getPeriodStatistics()).isNull();
        }

        @Test
        public void cachesKabaddiTotalsWhenNoneOfStatisticProvided() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();
            UfSportEventStatus ufStatus = withEmptyStatistics(kabaddiMatchFeedStatus());

            SportEventStatusDto statusDto = new SportEventStatusDto(ufStatus);
            populate(statusCache).fromMessage(with(matchUrn), statusDto);

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                noRefreshViaApi
            );

            val statistics = matchStatus.getStatistics();

            assertThat(statistics).totalsEqualToThoseIn(ufStatus);
            assertThat(statistics.getPeriodStatistics()).isNull();
        }
    }

    @Nested
    public class PopulatedFromSummary {

        private static final String STATISTICS_WITHOUT_2_TEAMS =
            "com.sportradar.unifiedodds.sdk.impl.StatisticsSources#statisticsWithout2Teams";

        private final boolean allowApiCalls = true;

        private final DataRouterManagerBuilder dataRouterManager = new DataRouterManagerBuilder();

        @ParameterizedTest
        @MethodSource(STATISTICS_WITHOUT_2_TEAMS)
        public void cachesNoStatisticsWhenSummaryDoesNotContainTotalStatsFor2Teams(
            SapiMatchStatistics statistics
        ) {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            summary.setStatistics(statistics);
            String matchId = summary.getSportEvent().getId();
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(
                    SportEventCaches.everyCompetitionRequestsSummaryToFetchStatus(
                        via(
                            dataRouterManager
                                .withSummaries(providing(in(ENGLISH), with(matchId), summary))
                                .with(dataRouter)
                                .build()
                        ),
                        in(ENGLISH)
                    )
                )
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                allowApiCalls
            );

            assertThat(matchStatus.getStatistics()).isNull();
        }

        @Test
        public void cachesEveryTotalStatistic() {
            val summary = withEveryTotalStatistic(soccerMatchGermanyScotlandEuro2024());
            String matchId = summary.getSportEvent().getId();
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(
                    SportEventCaches.everyCompetitionRequestsSummaryToFetchStatus(
                        via(
                            dataRouterManager
                                .withSummaries(providing(in(ENGLISH), with(matchId), summary))
                                .with(dataRouter)
                                .build()
                        ),
                        in(ENGLISH)
                    )
                )
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                allowApiCalls
            );

            val statistics = matchStatus.getStatistics();
            StatisticsAssert.assertThat(statistics).totalsEqualToThoseIn(summary.getStatistics());
            StatisticsAssert.assertThat(statistics).forPeriodsEqualToThoseIn(germanVsScotlandStats());
        }

        @Test
        public void cachesTotalStatisticsWhenNonProvided() {
            val summary = withEmptyTotalStatistics(soccerMatchGermanyScotlandEuro2024());
            String matchId = summary.getSportEvent().getId();
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(
                    SportEventCaches.everyCompetitionRequestsSummaryToFetchStatus(
                        via(
                            dataRouterManager
                                .withSummaries(providing(in(ENGLISH), with(matchId), summary))
                                .with(dataRouter)
                                .build()
                        ),
                        in(ENGLISH)
                    )
                )
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                allowApiCalls
            );

            val statistics = matchStatus.getStatistics();
            StatisticsAssert.assertThat(statistics).totalsEqualToThoseIn(summary.getStatistics());
            StatisticsAssert.assertThat(statistics).forPeriodsEqualToThoseIn(germanVsScotlandStats());
        }

        @Test
        public void cachesNullPeriodStatisticsWhenPeriodsNotProvided() {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            summary.setStatistics(germanVsScotlandStats());
            summary.getStatistics().setPeriods(null);

            String matchId = summary.getSportEvent().getId();
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(
                    SportEventCaches.everyCompetitionRequestsSummaryToFetchStatus(
                        via(
                            dataRouterManager
                                .withSummaries(providing(in(ENGLISH), with(matchId), summary))
                                .with(dataRouter)
                                .build()
                        ),
                        in(ENGLISH)
                    )
                )
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                allowApiCalls
            );

            val statistics = matchStatus.getStatistics();
            assertThat(statistics.getPeriodStatistics()).isNull();
        }
    }
}

class StatisticsSources {

    public static Stream<Arguments> statisticsWithout2Teams() {
        return Stream.of(
            Arguments.of(Named.of("null statistics", null)),
            Arguments.of(Named.of("without totals", new SapiMatchStatistics())),
            Arguments.of(Named.of("without any group of teams in totals", withoutSetOfTeams())),
            Arguments.of(Named.of("with multiple group of teams in totals", with2SetOfTeams())),
            Arguments.of(Named.of("with no teams in a group of teams", withNoTeams())),
            Arguments.of(Named.of("with 1 in a group of teams", with1Team())),
            Arguments.of(Named.of("with 3 in a group of teams", with3Team()))
        );
    }

    private static SapiMatchStatistics with3Team() {
        SapiMatchStatistics with3Team = withGermanyVsScotlandTotals(new SapiMatchStatistics());
        with3Team.getTotals().getTeams().get(0).getTeam().add(new SapiTeamStatistics());
        return with3Team;
    }

    private static SapiMatchStatistics with1Team() {
        SapiMatchStatistics with1Team = withGermanyVsScotlandTotals(new SapiMatchStatistics());
        with1Team.getTotals().getTeams().get(0).getTeam().remove(0);
        return with1Team;
    }

    private static SapiMatchStatistics withNoTeams() {
        SapiMatchStatistics withNoTeams = withGermanyVsScotlandTotals(new SapiMatchStatistics());
        withNoTeams.getTotals().getTeams().get(0).getTeam().clear();
        return withNoTeams;
    }

    private static SapiMatchStatistics with2SetOfTeams() {
        SapiMatchStatistics with2SetOfTeams = withGermanyVsScotlandTotals(new SapiMatchStatistics());
        with2SetOfTeams.getTotals().getTeams().add(new SapiStatisticsTeam());
        return with2SetOfTeams;
    }

    private static SapiMatchStatistics withoutSetOfTeams() {
        SapiMatchStatistics withoutSetOfTeams = withGermanyVsScotlandTotals(new SapiMatchStatistics());
        withoutSetOfTeams.getTotals().getTeams().clear();
        return withoutSetOfTeams;
    }
}
