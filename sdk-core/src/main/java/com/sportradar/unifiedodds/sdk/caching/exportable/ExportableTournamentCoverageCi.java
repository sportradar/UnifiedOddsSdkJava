package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableTournamentCoverageCi implements Serializable {

    private String liveCoverage;

    public ExportableTournamentCoverageCi(String liveCoverage) {
        this.liveCoverage = liveCoverage;
    }

    public String getLiveCoverage() {
        return liveCoverage;
    }

    public void setLiveCoverage(String liveCoverage) {
        this.liveCoverage = liveCoverage;
    }
}
