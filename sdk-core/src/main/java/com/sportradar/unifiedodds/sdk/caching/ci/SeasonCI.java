/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiSeasonExtended;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSeasonCi;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;

/**
 * A season representation used by caching components
 */
@SuppressWarnings({ "IllegalType" })
public class SeasonCi extends SportEntityCi {

    /**
     * A {@link HashMap} containing season names in different languages
     */
    private final HashMap<Locale, String> name;

    /**
     * The {@link Date} specifying the start date of the season
     */
    private Date startDate;

    /**
     * The {@link Date} specifying the end date of the season
     */
    private Date endDate;

    /**
     * The {@link String} representation the year of the season
     */
    private String year;

    /**
     * The associated tournament identifier
     */
    private Urn tournamentId;

    private final List<Locale> cachedLocales;

    /**
     * Initializes a new instance of the {@link SeasonCi} class
     *
     * @param season - {@link SapiSeasonExtended} containing information about the season
     * @param locale - {@link Locale} specifying the language of the <i>season</i>
     */
    public SeasonCi(SapiSeasonExtended season, Locale locale) {
        super(Urn.parse(season.getId()));
        Preconditions.checkNotNull(season);
        Preconditions.checkNotNull(locale);

        name = new HashMap<>();
        cachedLocales = Collections.synchronizedList(new ArrayList<>());
        merge(season, locale);
    }

    public SeasonCi(ExportableSeasonCi exportable) {
        super(Urn.parse(exportable.getId()));
        this.name = new HashMap<>(exportable.getNames());
        this.startDate = exportable.getStartDate();
        this.endDate = exportable.getEndDate();
        this.year = exportable.getYear();
        this.tournamentId =
            exportable.getTournamentId() != null ? Urn.parse(exportable.getTournamentId()) : null;
        this.cachedLocales = Collections.synchronizedList(new ArrayList<>(exportable.getCachedLocales()));
    }

    /**
     * Merges the information from the provided {@link SapiSeasonExtended} into the current instance
     *
     * @param season - {@link SapiSeasonExtended} containing information about the season
     * @param locale - {@link Locale} specifying the language of the <i>season</i>
     */
    public void merge(SapiSeasonExtended season, Locale locale) {
        Preconditions.checkNotNull(season);
        Preconditions.checkNotNull(locale);

        if (season.getStartDate() != null) {
            startDate = SdkHelper.toDate(season.getStartDate());
        }

        if (season.getEndDate() != null) {
            endDate = SdkHelper.toDate(season.getEndDate());
        }

        if (season.getYear() != null) {
            year = season.getYear();
        }

        if (season.getTournamentId() != null) {
            tournamentId = Urn.parse(season.getTournamentId());
        }

        if (season.getStartTime() != null) {
            startDate =
                SdkHelper.combineDateAndTime(
                    SdkHelper.toDate(season.getStartDate()),
                    SdkHelper.toDate(season.getStartTime())
                );
        }

        if (season.getEndTime() != null) {
            endDate =
                SdkHelper.combineDateAndTime(
                    SdkHelper.toDate(season.getEndDate()),
                    SdkHelper.toDate(season.getEndTime())
                );
        }

        name.put(locale, season.getName());
        cachedLocales.add(locale);
    }

    /**
     * Merges the information from the provided {@link SeasonCi} into the current instance
     *
     * @param season - {@link SeasonCi} containing information about the season
     * @param locale - {@link Locale} specifying the language of the <i>season</i>
     */
    public void merge(SeasonCi season, Locale locale) {
        Preconditions.checkNotNull(season);
        Preconditions.checkNotNull(locale);

        if (season.getStartDate() != null) {
            startDate = season.getStartDate();
        }

        if (season.getEndDate() != null) {
            endDate = season.getEndDate();
        }

        if (year != null) {
            year = season.getYear();
        }

        name.put(locale, season.getName(locale) == null ? "" : season.getName(locale));
    }

    /**
     * Returns the {@link Date} specifying the start date of the season
     *
     * @return - the {@link Date} specifying the start date of the season
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Returns the {@link Date} specifying the end date of the season
     *
     * @return - the {@link Date} specifying the end date of the season
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Returns the {@link String} representation the year of the season
     *
     * @return - the {@link String} representation the year of the season
     */
    public String getYear() {
        return year;
    }

    /**
     * Returns the associated tournament identifier
     *
     * @return the associated tournament identifier
     */
    public Urn getTournamentId() {
        return tournamentId;
    }

    /**
     * Returns the name of the season in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned abbreviation
     * @return - The name of the season in the specified language if it exists. Null otherwise.
     */
    public String getName(Locale locale) {
        return name.getOrDefault(locale, null);
    }

    public boolean hasTranslationsFor(List<Locale> locales) {
        Preconditions.checkNotNull(locales);

        return cachedLocales.containsAll(locales);
    }

    public ExportableSeasonCi export() {
        return new ExportableSeasonCi(
            getId().toString(),
            new HashMap<>(name),
            startDate,
            endDate,
            year,
            tournamentId != null ? tournamentId.toString() : null,
            new ArrayList<>(cachedLocales)
        );
    }
}
