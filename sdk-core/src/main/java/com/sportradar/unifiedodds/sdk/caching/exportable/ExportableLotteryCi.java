/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.*;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableLotteryCi extends ExportableSportEventCi {

    private Locale defaultLocale;
    private String categoryId;
    private ExportableBonusInfoCi bonusInfo;
    private ExportableDrawInfoCi drawInfo;
    private List<String> scheduledDraws;
    private Set<Locale> cachedLocales;

    public ExportableLotteryCi(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy,
        Locale defaultLocale,
        String categoryId,
        ExportableBonusInfoCi bonusInfo,
        ExportableDrawInfoCi drawInfo,
        List<String> scheduledDraws,
        Set<Locale> cachedLocales
    ) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy);
        this.defaultLocale = defaultLocale;
        this.categoryId = categoryId;
        this.bonusInfo = bonusInfo;
        this.drawInfo = drawInfo;
        this.scheduledDraws = scheduledDraws;
        this.cachedLocales = cachedLocales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ExportableBonusInfoCi getBonusInfo() {
        return bonusInfo;
    }

    public void setBonusInfo(ExportableBonusInfoCi bonusInfo) {
        this.bonusInfo = bonusInfo;
    }

    public ExportableDrawInfoCi getDrawInfo() {
        return drawInfo;
    }

    public void setDrawInfo(ExportableDrawInfoCi drawInfo) {
        this.drawInfo = drawInfo;
    }

    public List<String> getScheduledDraws() {
        return scheduledDraws;
    }

    public void setScheduledDraws(List<String> scheduledDraws) {
        this.scheduledDraws = scheduledDraws;
    }

    public Set<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(Set<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
