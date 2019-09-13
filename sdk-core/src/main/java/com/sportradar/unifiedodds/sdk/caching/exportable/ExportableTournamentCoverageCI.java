package com.sportradar.unifiedodds.sdk.caching.exportable;

public class ExportableTournamentCoverageCI {
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
