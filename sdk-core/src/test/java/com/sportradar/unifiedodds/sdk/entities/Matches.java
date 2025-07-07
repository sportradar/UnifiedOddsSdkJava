/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

public final class Matches {

    public static Match matchWithHomeAndAwayCompetitors(
        TeamCompetitor homeCompetitor,
        TeamCompetitor awayCompetitor
    ) {
        return new MatchStub() {
            @Override
            public TeamCompetitor getHomeCompetitor() {
                return homeCompetitor;
            }

            @Override
            public TeamCompetitor getAwayCompetitor() {
                return awayCompetitor;
            }
        };
    }
}
