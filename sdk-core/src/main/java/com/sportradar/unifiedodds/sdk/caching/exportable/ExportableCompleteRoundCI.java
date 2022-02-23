package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableCompleteRoundCI implements Serializable {
    private Map<Locale, String> names;
    private Map<Locale, String> groupNames;
    private Map<Locale, String> phaseOrGroupLongNames;
    private String type;
    private String group;
    private String groupId;
    private String otherMatchId;
    private Integer number;
    private Integer cupRoundMatches;
    private Integer cupRoundMatchNumber;
    private Integer betradarId;
    private String phase;
    private String betradarName;
    private List<Locale> cachedLocales;

    public ExportableCompleteRoundCI(Map<Locale, String> names,
                                     Map<Locale, String> groupNames,
                                     Map<Locale, String> phaseOrGroupLongNames,
                                     String type,
                                     String group,
                                     String groupId,
                                     String otherMatchId,
                                     Integer number,
                                     Integer cupRoundMatches,
                                     Integer cupRoundMatchNumber,
                                     Integer betradarId,
                                     String phase,
                                     String betradarName,
                                     List<Locale> cachedLocales) {
        this.names = names;
        this.groupNames = groupNames;
        this.phaseOrGroupLongNames = phaseOrGroupLongNames;
        this.type = type;
        this.group = group;
        this.groupId = groupId;
        this.otherMatchId = otherMatchId;
        this.number = number;
        this.cupRoundMatches = cupRoundMatches;
        this.cupRoundMatchNumber = cupRoundMatchNumber;
        this.betradarId = betradarId;
        this.phase = phase;
        this.betradarName = betradarName;
        this.cachedLocales = cachedLocales;
    }

    public Map<Locale, String> getNames() {
        return names;
    }

    public void setNames(Map<Locale, String> names) {
        this.names = names;
    }

    public Map<Locale, String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(Map<Locale, String> groupNames) {
        this.groupNames = groupNames;
    }

    public Map<Locale, String> getPhaseOrGroupLongNames() {
        return phaseOrGroupLongNames;
    }

    public void setPhaseOrGroupLongNames(Map<Locale, String> phaseOrGroupLongNames) {
        this.phaseOrGroupLongNames = phaseOrGroupLongNames;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOtherMatchId() {
        return otherMatchId;
    }

    public void setOtherMatchId(String otherMatchId) {
        this.otherMatchId = otherMatchId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getCupRoundMatches() {
        return cupRoundMatches;
    }

    public void setCupRoundMatches(Integer cupRoundMatches) {
        this.cupRoundMatches = cupRoundMatches;
    }

    public Integer getCupRoundMatchNumber() {
        return cupRoundMatchNumber;
    }

    public void setCupRoundMatchNumber(Integer cupRoundMatchNumber) {
        this.cupRoundMatchNumber = cupRoundMatchNumber;
    }

    public Integer getBetradarId() {
        return betradarId;
    }

    public void setBetradarId(Integer betradarId) {
        this.betradarId = betradarId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getBetradarName() { return betradarName; }

    public void setBetradarName(String betradarName) {
        this.betradarName = betradarName;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
