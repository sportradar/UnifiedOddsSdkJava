package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.CoverageInfo;
import com.sportradar.unifiedodds.sdk.entities.CoveredFrom;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField" })
public class ExportableCoverageInfoCI implements Serializable {

    private String level;
    private boolean isLive;
    private List<String> includes;
    private CoveredFrom coveredFrom;

    public ExportableCoverageInfoCI(
        String level,
        boolean isLive,
        List<String> includes,
        CoveredFrom coveredFrom
    ) {
        this.level = level;
        this.isLive = isLive;
        this.includes = includes;
        this.coveredFrom = coveredFrom;
    }

    public ExportableCoverageInfoCI(CoverageInfo coverageInfo) {
        this.level = coverageInfo.getLevel();
        this.isLive = coverageInfo.isLive();
        this.includes = new ArrayList<>(coverageInfo.getIncludes());
        this.coveredFrom = coverageInfo.getCoveredFrom();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public CoveredFrom getCoveredFrom() {
        return coveredFrom;
    }

    public void setCoveredFrom(CoveredFrom coveredFrom) {
        this.coveredFrom = coveredFrom;
    }
}
