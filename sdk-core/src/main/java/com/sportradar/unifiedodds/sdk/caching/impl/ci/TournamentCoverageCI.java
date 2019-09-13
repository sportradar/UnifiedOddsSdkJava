/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentLiveCoverageInfo;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableTournamentCoverageCI;

/**
 * Created on 25/10/2017.
 * // TODO @eti: Javadoc
 */
public class TournamentCoverageCI {
    private final String liveCoverage;

    TournamentCoverageCI(SAPITournamentLiveCoverageInfo coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);

        liveCoverage = coverageInfo.getLiveCoverage();
    }

    TournamentCoverageCI(ExportableTournamentCoverageCI exportable) {
        Preconditions.checkNotNull(exportable);
        liveCoverage = exportable.getLiveCoverage();
    }

    public String getLiveCoverage() {
        return liveCoverage;
    }

    public ExportableTournamentCoverageCI export() {
        return new ExportableTournamentCoverageCI(
                liveCoverage
        );
    }
}
