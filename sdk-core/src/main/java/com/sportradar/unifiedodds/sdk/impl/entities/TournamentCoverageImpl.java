/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.TournamentCoverageCI;
import com.sportradar.unifiedodds.sdk.entities.TournamentCoverage;

/**
 * Created on 25/10/2017.
 * // TODO @eti: Javadoc
 */
class TournamentCoverageImpl implements TournamentCoverage {
    private final boolean isLiveCoverage;

    TournamentCoverageImpl(TournamentCoverageCI tournamentCoverage) {
        Preconditions.checkNotNull(tournamentCoverage);

        isLiveCoverage = tournamentCoverage.getLiveCoverage() != null && tournamentCoverage.getLiveCoverage().equals("true");
    }

    /**
     * An indication if live coverage is available
     *
     * @return <code>true</code> if the live coverage is available; otherwise <code>false</code>
     */
    @Override
    public boolean isLiveCoverage() {
        return isLiveCoverage;
    }
}
