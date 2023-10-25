/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentLiveCoverageInfo;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableTournamentCoverageCi;

/**
 * Created on 25/10/2017.
 * // TODO @eti: Javadoc
 */
public class TournamentCoverageCi {

    private final String liveCoverage;

    TournamentCoverageCi(SapiTournamentLiveCoverageInfo coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);

        liveCoverage = coverageInfo.getLiveCoverage();
    }

    TournamentCoverageCi(ExportableTournamentCoverageCi exportable) {
        Preconditions.checkNotNull(exportable);
        liveCoverage = exportable.getLiveCoverage();
    }

    public String getLiveCoverage() {
        return liveCoverage;
    }

    public ExportableTournamentCoverageCi export() {
        return new ExportableTournamentCoverageCi(liveCoverage);
    }
}
