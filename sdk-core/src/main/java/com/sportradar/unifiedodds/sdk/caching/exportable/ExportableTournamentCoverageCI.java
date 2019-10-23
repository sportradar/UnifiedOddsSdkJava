package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

public class ExportableTournamentCoverageCI implements Serializable {
    private String liveCoverage;

    public ExportableTournamentCoverageCI(String liveCoverage) {
        this.liveCoverage = liveCoverage;
    }

    public String getLiveCoverage() {
        return liveCoverage;
    }

    public void setLiveCoverage(String liveCoverage) {
        this.liveCoverage = liveCoverage;
    }
}
