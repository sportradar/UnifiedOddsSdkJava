/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiTeamStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.TeamStatistics;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class TeamStatisticsAssert extends AbstractAssert<TeamStatisticsAssert, TeamStatistics> {

    protected TeamStatisticsAssert(TeamStatistics teamStatistics) {
        super(teamStatistics, TeamStatisticsAssert.class);
    }

    public static TeamStatisticsAssert assertThat(TeamStatistics statistics) {
        return new TeamStatisticsAssert(statistics);
    }

    public TeamStatisticsAssert isEqualTo(SapiTeamStatistics sapiTeamStatistics) {
        val sapiStats = sapiTeamStatistics.getStatistics();
        Assertions.assertThat(actual.getCornerKicks()).hasToString(sapiStats.getCornerKicks());
        Assertions.assertThat(actual.getCards()).hasToString(sapiStats.getCards());
        Assertions.assertThat(actual.getYellowCards()).hasToString(sapiStats.getYellowCards());
        Assertions.assertThat(actual.getRedCards()).hasToString(sapiStats.getRedCards());
        return this;
    }
}
