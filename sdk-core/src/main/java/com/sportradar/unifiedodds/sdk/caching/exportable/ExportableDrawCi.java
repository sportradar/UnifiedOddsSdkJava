/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.DrawStatus;
import java.util.*;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableDrawCi extends ExportableSportEventCi {

    private Locale defaultLocale;
    private String lotteryId;
    private DrawStatus status;
    private List<ExportableDrawResultCi> results;
    private Integer displayId;
    private Set<Locale> cachedLocales;

    public ExportableDrawCi(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy,
        Locale defaultLocale,
        String lotteryId,
        DrawStatus status,
        List<ExportableDrawResultCi> results,
        Integer displayId,
        Set<Locale> cachedLocales
    ) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy);
        this.defaultLocale = defaultLocale;
        this.lotteryId = lotteryId;
        this.status = status;
        this.results = results;
        this.displayId = displayId;
        this.cachedLocales = cachedLocales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public DrawStatus getStatus() {
        return status;
    }

    public void setStatus(DrawStatus status) {
        this.status = status;
    }

    public List<ExportableDrawResultCi> getResults() {
        return results;
    }

    public void setResults(List<ExportableDrawResultCi> results) {
        this.results = results;
    }

    public Integer getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Integer displayId) {
        this.displayId = displayId;
    }

    public Set<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(Set<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
