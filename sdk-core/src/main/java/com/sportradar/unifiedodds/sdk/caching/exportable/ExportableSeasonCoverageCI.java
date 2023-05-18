package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField" })
public class ExportableSeasonCoverageCI implements Serializable {

    private String seasonId;
    private String maxCoverageLevel;
    private String minCoverageLevel;
    private Integer maxCovered;
    private int played;
    private int scheduled;

    public ExportableSeasonCoverageCI(
        String seasonId,
        String maxCoverageLevel,
        String minCoverageLevel,
        Integer maxCovered,
        int played,
        int scheduled
    ) {
        this.seasonId = seasonId;
        this.maxCoverageLevel = maxCoverageLevel;
        this.minCoverageLevel = minCoverageLevel;
        this.maxCovered = maxCovered;
        this.played = played;
        this.scheduled = scheduled;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getMaxCoverageLevel() {
        return maxCoverageLevel;
    }

    public void setMaxCoverageLevel(String maxCoverageLevel) {
        this.maxCoverageLevel = maxCoverageLevel;
    }

    public String getMinCoverageLevel() {
        return minCoverageLevel;
    }

    public void setMinCoverageLevel(String minCoverageLevel) {
        this.minCoverageLevel = minCoverageLevel;
    }

    public Integer getMaxCovered() {
        return maxCovered;
    }

    public void setMaxCovered(Integer maxCovered) {
        this.maxCovered = maxCovered;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getScheduled() {
        return scheduled;
    }

    public void setScheduled(int scheduled) {
        this.scheduled = scheduled;
    }
}
