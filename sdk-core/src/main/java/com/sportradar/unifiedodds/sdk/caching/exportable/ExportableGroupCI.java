package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ExportableGroupCI implements Serializable {
    private String id;
    private String name;
    private List<String> competitorIds;
    private Map<String, Map<String, String>> competitorsReferences;

    public ExportableGroupCI(String id, String name, List<String> competitorIds, Map<String, Map<String, String>> competitorsReferences) {
        this.id = id;
        this.name = name;
        this.competitorIds = competitorIds;
        this.competitorsReferences = competitorsReferences;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCompetitorIds() {
        return competitorIds;
    }

    public void setCompetitorIds(List<String> competitorIds) {
        this.competitorIds = competitorIds;
    }

    public Map<String, Map<String, String>> getCompetitorsReferences() {
        return competitorsReferences;
    }

    public void setCompetitorsReferences(Map<String, Map<String, String>> competitorsReferences) {
        this.competitorsReferences = competitorsReferences;
    }
}
