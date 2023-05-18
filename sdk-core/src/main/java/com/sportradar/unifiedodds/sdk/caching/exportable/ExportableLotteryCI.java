/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.*;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField", "ParameterNumber" })
public class ExportableLotteryCI extends ExportableSportEventCI {

    private Locale defaultLocale;
    private String categoryId;
    private ExportableBonusInfoCI bonusInfo;
    private ExportableDrawInfoCI drawInfo;
    private List<String> scheduledDraws;
    private Set<Locale> cachedLocales;

    public ExportableLotteryCI(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy,
        Locale defaultLocale,
        String categoryId,
        ExportableBonusInfoCI bonusInfo,
        ExportableDrawInfoCI drawInfo,
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

    public ExportableBonusInfoCI getBonusInfo() {
        return bonusInfo;
    }

    public void setBonusInfo(ExportableBonusInfoCI bonusInfo) {
        this.bonusInfo = bonusInfo;
    }

    public ExportableDrawInfoCI getDrawInfo() {
        return drawInfo;
    }

    public void setDrawInfo(ExportableDrawInfoCI drawInfo) {
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
