/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.entities.HomeAway.Away;
import static com.sportradar.unifiedodds.sdk.entities.HomeAway.Home;

import com.sportradar.uf.datamodel.UfSportEventStatus;
import com.sportradar.uf.datamodel.UfStatisticsScoreType;
import com.sportradar.uf.datamodel.UfStatisticsType;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.PeriodStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.*;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class StatisticsAssert extends AbstractAssert<StatisticsAssert, MatchStatistics> {

    public static final int THE_ONLY_TEAMS_BLOCK = 0;

    private StatisticsAssert(MatchStatistics statistics) {
        super(statistics, StatisticsAssert.class);
    }

    public static StatisticsAssert assertThat(MatchStatistics statistics) {
        return new StatisticsAssert(statistics);
    }

    public void totalsEqualToThoseIn(UfSportEventStatus feedStats) {
        awayHasTotalStatsEqualTo(feedStats);
        homeHasTotalStatsEqualTo(feedStats);
    }

    public void totalsEqualToThoseIn(SapiMatchStatistics matchStats) {
        final int firstInList = 0;
        final int secondInList = 1;
        Statistics expectedFirstTeamStats = expectedTotalStatsForTeam(firstInList, matchStats.getTotals());
        Statistics expectedSecondTeamStats = expectedTotalStatsForTeam(secondInList, matchStats.getTotals());
        Statistics actualHomeStats = actualTotalStatsFor(Home);
        Statistics actualAwayStats = actualTotalStatsFor(Away);

        if (firstTeamRepresentsHome(actualHomeStats, expectedFirstTeamStats)) {
            Assertions.assertThat(actualHomeStats).isEqualTo(expectedFirstTeamStats);
            Assertions.assertThat(actualAwayStats).isEqualTo(expectedSecondTeamStats);
        } else {
            Assertions.assertThat(actualHomeStats).isEqualTo(expectedSecondTeamStats);
            Assertions.assertThat(actualAwayStats).isEqualTo(expectedFirstTeamStats);
        }
    }

    public void forPeriodsEqualToThoseIn(SapiMatchStatistics matchStats) {
        List<SapiMatchPeriod> allPeriods = matchStats.getPeriods().getPeriod();
        final int firstInList = 0;
        final int secondInList = 1;
        List<Statistics> expectedFirstTeamStats = expectedPeriodStatsForTeam(firstInList, allPeriods);
        List<Statistics> expectedSecondTeamStats = expectedPeriodStatsForTeam(secondInList, allPeriods);
        List<Statistics> actualHomeStats = actualPeriodStatsForTeam(Home);
        List<Statistics> actualAwayStats = actualPeriodStatsForTeam(Away);

        if (firstTeamRepresentsHome(actualHomeStats, expectedFirstTeamStats)) {
            Assertions.assertThat(actualHomeStats).isEqualTo(expectedFirstTeamStats);
            Assertions.assertThat(actualAwayStats).isEqualTo(expectedSecondTeamStats);
        } else {
            Assertions.assertThat(actualHomeStats).isEqualTo(expectedSecondTeamStats);
            Assertions.assertThat(actualAwayStats).isEqualTo(expectedFirstTeamStats);
        }
    }

    private static boolean firstTeamRepresentsHome(
        Statistics actualHomeStats,
        Statistics expectedFirstTeamStats
    ) {
        return actualHomeStats.equals(expectedFirstTeamStats);
    }

    private static boolean firstTeamRepresentsHome(
        List<Statistics> actualHomeStats,
        List<Statistics> expectedFirstTeamStats
    ) {
        return actualHomeStats.equals(expectedFirstTeamStats);
    }

    private void homeHasTotalStatsEqualTo(UfSportEventStatus ufSportEventStatus) {
        val expectedHomeStats = expectedTotalStatsForHome(ufSportEventStatus.getStatistics());
        val actualHomeStats = actualTotalStatsFor(Home);

        Assertions.assertThat(actualHomeStats).isEqualTo(expectedHomeStats);
    }

    private void awayHasTotalStatsEqualTo(UfSportEventStatus ufSportEventStatus) {
        val expectedAwayStats = expectedTotalStatsForAway(ufSportEventStatus.getStatistics());
        val actualAwayStats = actualTotalStatsFor(Away);

        Assertions.assertThat(actualAwayStats).isEqualTo(expectedAwayStats);
    }

    private Statistics actualTotalStatsFor(HomeAway homeAway) {
        val stats = actual
            .getTotalStatistics()
            .stream()
            .filter(s -> s.getHomeAway() == homeAway)
            .findFirst()
            .get();
        return Statistics
            .builder()
            .cards(stats.getCards())
            .cornerKicks(stats.getCornerKicks())
            .yellowCards(stats.getYellowCards())
            .redCards(stats.getRedCards())
            .yellowRedCards(stats.getYellowRedCards())
            .greenCards(stats.getGreenCards())
            .build();
    }

    private Statistics expectedTotalStatsForHome(UfStatisticsType statistics) {
        return Statistics
            .builder()
            .cornerKicks(statistics.getCorners(), UfStatisticsScoreType::getHome)
            .yellowCards(statistics.getYellowCards(), UfStatisticsScoreType::getHome)
            .redCards(statistics.getRedCards(), UfStatisticsScoreType::getHome)
            .yellowRedCards(statistics.getYellowRedCards(), UfStatisticsScoreType::getHome)
            .greenCards(statistics.getGreenCards(), UfStatisticsScoreType::getHome)
            .cardsAsSumOfAllCards()
            .build();
    }

    private Statistics expectedTotalStatsForAway(UfStatisticsType statistics) {
        return Statistics
            .builder()
            .cornerKicks(statistics.getCorners(), UfStatisticsScoreType::getAway)
            .yellowCards(statistics.getYellowCards(), UfStatisticsScoreType::getAway)
            .redCards(statistics.getRedCards(), UfStatisticsScoreType::getAway)
            .yellowRedCards(statistics.getYellowRedCards(), UfStatisticsScoreType::getAway)
            .greenCards(statistics.getGreenCards(), UfStatisticsScoreType::getAway)
            .cardsAsSumOfAllCards()
            .build();
    }

    private List<Statistics> actualPeriodStatsForTeam(HomeAway homeAway) {
        List<PeriodStatistics> actualPeriods = actual.getPeriodStatistics();
        int size = actualPeriods.size();
        List<Statistics> actualHomeStats = IntStream
            .range(0, size)
            .mapToObj(i ->
                actualPeriods
                    .get(i)
                    .getTeamStatistics()
                    .stream()
                    .filter(t -> t.getHomeAway() == homeAway)
                    .findFirst()
                    .get()
            )
            .map(stats ->
                new Statistics.StatisticsBuilder()
                    .cards(stats.getCards())
                    .redCards(stats.getRedCards())
                    .cornerKicks(stats.getCornerKicks())
                    .yellowCards(stats.getYellowCards())
                    .yellowRedCards(stats.getYellowRedCards())
                    .build()
            )
            .collect(Collectors.toList());
        return actualHomeStats;
    }

    private static Statistics expectedTotalStatsForTeam(int index, SapiStatisticsTotals totals) {
        val teamStats = totals.getTeams().get(THE_ONLY_TEAMS_BLOCK).getTeam().get(index).getStatistics();
        return new Statistics.StatisticsBuilder()
            .cards(teamStats.getCards())
            .redCards(teamStats.getRedCards())
            .cornerKicks(teamStats.getCornerKicks())
            .yellowCards(teamStats.getYellowCards())
            .yellowRedCards(teamStats.getYellowRedCards())
            .build();
    }

    private static List<Statistics> expectedPeriodStatsForTeam(int index, List<SapiMatchPeriod> periods) {
        return IntStream
            .range(0, periods.size())
            .mapToObj(i ->
                periods.get(i).getTeams().get(THE_ONLY_TEAMS_BLOCK).getTeam().get(index).getStatistics()
            )
            .map(firstTeamStats ->
                new Statistics.StatisticsBuilder()
                    .cards(firstTeamStats.getCards())
                    .redCards(firstTeamStats.getRedCards())
                    .cornerKicks(firstTeamStats.getCornerKicks())
                    .yellowCards(firstTeamStats.getYellowCards())
                    .yellowRedCards(firstTeamStats.getYellowRedCards())
                    .build()
            )
            .collect(Collectors.toList());
    }

    @Builder
    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    private static class Statistics {

        private String cards;
        private String cornerKicks;
        private String yellowCards;
        private String yellowRedCards;
        private String redCards;
        private String greenCards;

        @SuppressWarnings("HiddenField")
        public static class StatisticsBuilder {

            private String cards;
            private String cornerKicks;
            private String yellowCards;
            private String yellowRedCards;
            private String redCards;
            private String greenCards;

            public StatisticsBuilder cards(String cards) {
                this.cards = cards;
                return this;
            }

            public StatisticsBuilder cards(Integer cards) {
                this.cards = cards == null ? null : cards + "";
                return this;
            }

            public StatisticsBuilder cards(
                UfStatisticsScoreType cards,
                Function<UfStatisticsScoreType, Integer> extract
            ) {
                this.cards = cards == null ? null : extract.apply(cards) + "";
                return this;
            }

            public StatisticsBuilder cornerKicks(String cornerKicks) {
                this.cornerKicks = cornerKicks;
                return this;
            }

            public StatisticsBuilder cornerKicks(Integer cornerKicks) {
                this.cornerKicks = cornerKicks == null ? null : cornerKicks + "";
                return this;
            }

            public StatisticsBuilder cornerKicks(
                UfStatisticsScoreType cornerKicks,
                Function<UfStatisticsScoreType, Integer> extract
            ) {
                this.cornerKicks = cornerKicks == null ? null : extract.apply(cornerKicks) + "";
                return this;
            }

            public StatisticsBuilder yellowCards(String yellowCards) {
                this.yellowCards = yellowCards;
                return this;
            }

            public StatisticsBuilder yellowCards(Integer yellowCards) {
                this.yellowCards = yellowCards == null ? null : yellowCards + "";
                return this;
            }

            public StatisticsBuilder yellowCards(
                UfStatisticsScoreType yellowCards,
                Function<UfStatisticsScoreType, Integer> extract
            ) {
                this.yellowCards = yellowCards == null ? null : extract.apply(yellowCards) + "";
                return this;
            }

            public StatisticsBuilder yellowRedCards(String yellowRedCards) {
                this.yellowRedCards = yellowRedCards;
                return this;
            }

            public StatisticsBuilder yellowRedCards(Integer yellowRedCards) {
                this.yellowRedCards = yellowRedCards == null ? null : yellowRedCards + "";
                return this;
            }

            public StatisticsBuilder yellowRedCards(
                UfStatisticsScoreType yellowRedCards,
                Function<UfStatisticsScoreType, Integer> extract
            ) {
                this.yellowRedCards = yellowRedCards == null ? null : extract.apply(yellowRedCards) + "";
                return this;
            }

            public StatisticsBuilder redCards(String redCards) {
                this.redCards = redCards;
                return this;
            }

            public StatisticsBuilder redCards(Integer redCards) {
                this.redCards = redCards == null ? null : redCards + "";
                return this;
            }

            public StatisticsBuilder redCards(
                UfStatisticsScoreType redCards,
                Function<UfStatisticsScoreType, Integer> extract
            ) {
                this.redCards = redCards == null ? null : extract.apply(redCards) + "";
                return this;
            }

            public StatisticsBuilder greenCards(String greenCards) {
                this.greenCards = greenCards;
                return this;
            }

            public StatisticsBuilder greenCards(Integer greenCards) {
                this.greenCards = greenCards == null ? null : greenCards + "";
                return this;
            }

            public StatisticsBuilder greenCards(
                UfStatisticsScoreType greenCards,
                Function<UfStatisticsScoreType, Integer> extract
            ) {
                this.greenCards = greenCards == null ? null : extract.apply(greenCards) + "";
                return this;
            }

            public StatisticsBuilder cardsAsSumOfAllCards() {
                if (atLeastOneCardStatisticProvided()) {
                    this.cards = sumCards();
                }
                return this;
            }

            private String sumCards() {
                return String.valueOf(
                    parse(yellowCards) + parse(yellowRedCards) + parse(redCards) + parse(greenCards)
                );
            }

            @SuppressWarnings({ "BooleanExpressionComplexity", "UnnecessaryParentheses" })
            private boolean atLeastOneCardStatisticProvided() {
                return (
                    yellowCards != null || yellowRedCards != null || redCards != null || greenCards != null
                );
            }

            private int parse(String number) {
                if (number == null) {
                    return 0;
                } else {
                    return Integer.parseInt(number);
                }
            }

            public Statistics build() {
                return new Statistics(cards, cornerKicks, yellowCards, yellowRedCards, redCards, greenCards);
            }
        }
    }
}
