/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.utils.generic.testing.AnyEnumValue.anyFrom;

import com.sportradar.uf.datamodel.*;
import java.math.BigDecimal;
import lombok.val;

@SuppressWarnings("MagicNumber")
public class UfSportEventStatuses {

    public static UfSportEventStatus soccerMatchFeedStatus() {
        val stats = new UfStatisticsType();
        stats.setCorners(statistic(5, 1));
        stats.setRedCards(statistic(1, 2));
        stats.setYellowCards(statistic(3, 1));
        UfSportEventStatus result = new UfSportEventStatus();
        result.setStatistics(stats);
        result.setStatus(anyFrom(UfEventStatusStatus.class));
        return result;
    }

    public static UfSportEventStatus kabaddiMatchFeedStatus() {
        UfSportEventStatus status = new UfSportEventStatus();
        status.setStatus(anyFrom(UfEventStatusStatus.class));
        status.setMatchStatus(100);
        status.setHomeScore(new BigDecimal(44));
        status.setAwayScore(new BigDecimal(44));
        UfResultsType results = new UfResultsType();
        results.getResult().add(new UfResultType());
        results.getResult().get(0).setMatchStatusCode(100);
        results.getResult().get(0).setHomeScore(new BigDecimal(44));
        results.getResult().get(0).setAwayScore(new BigDecimal(44));
        status.setResults(results);
        return status;
    }

    public static UfSportEventStatus withEveryStatistic(UfSportEventStatus status) {
        status.setStatistics(everyStatistics());
        return status;
    }

    public static UfSportEventStatus withEmptyStatistics(UfSportEventStatus status) {
        status.setStatistics(new UfStatisticsType());
        return status;
    }

    private static UfStatisticsType everyStatistics() {
        val stats = new UfStatisticsType();
        stats.setCorners(statistic(1, 0));
        stats.setYellowCards(statistic(1, 2));
        stats.setYellowRedCards(statistic(2, 3));
        stats.setRedCards(statistic(3, 4));
        stats.setGreenCards(statistic(5, 6));
        return stats;
    }

    public static UfStatisticsScoreType statistic(int home, int away) {
        UfStatisticsScoreType score = new UfStatisticsScoreType();
        score.setHome(home);
        score.setAway(away);
        return score;
    }
}
