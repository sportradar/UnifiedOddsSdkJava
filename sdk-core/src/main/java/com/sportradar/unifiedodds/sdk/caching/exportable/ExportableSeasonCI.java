package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField", "ParameterNumber" })
public class ExportableSeasonCI extends ExportableCI {

    private Date startDate;
    private Date endDate;
    private String year;
    private String tournamentId;
    private List<Locale> cachedLocales;

    public ExportableSeasonCI(
        String id,
        Map<Locale, String> names,
        Date startDate,
        Date endDate,
        String year,
        String tournamentId,
        List<Locale> cachedLocales
    ) {
        super(id, names);
        this.startDate = startDate;
        this.endDate = endDate;
        this.year = year;
        this.tournamentId = tournamentId;
        this.cachedLocales = cachedLocales;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
