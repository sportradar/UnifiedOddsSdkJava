/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.unifiedodds.sdk.entities.StageType;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableTournamentStageCI extends ExportableStageCI {
    private Locale defaultLocale;
    private List<Locale> cachedLocales;

    public ExportableTournamentStageCI(String id, Map<Locale, String> names, Date scheduled, Date scheduledEnd,
                                       Boolean startTimeTbd, String replacedBy, BookingStatus bookingStatus,
                                       List<String> competitorIds, ExportableVenueCI venue,
                                       ExportableSportEventConditionsCI conditions,
                                       Map<String, Map<String, String>> competitorsReferences, String parentStageId,
                                       List<String> stagesIds, StageType stageType, String categoryId,
                                       Locale defaultLocale, List<Locale> cachedLocales, String liveOdds,
                                       SportEventType sportEventType,
                                       List<String> competitorVirtual) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy, bookingStatus, competitorIds, venue,
              conditions, competitorsReferences, parentStageId, stagesIds, stageType, categoryId, liveOdds,
              sportEventType, null, competitorVirtual);
        this.defaultLocale = defaultLocale;
        this.cachedLocales = cachedLocales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
