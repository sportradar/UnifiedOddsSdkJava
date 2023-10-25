/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCi;
import com.sportradar.unifiedodds.sdk.entities.SeasonInfo;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides season information about an entity (sport, category, season, ...)
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class SeasonInfoImpl implements SeasonInfo {

    /**
     * The unique {@link Urn} identifier
     */
    private final Urn id;

    /**
     * An unmodifiable {@link Map} containing translated names
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<Locale, String> names;

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

    /**
     * Initializes a new instance of {@link SeasonInfoImpl}
     *
     * @param seasonCi - a {@link SeasonCi} used to build the instance
     * @param locales - a {@link List} of locales supported by the instance
     */
    SeasonInfoImpl(SeasonCi seasonCi, List<Locale> locales) {
        Preconditions.checkNotNull(seasonCi);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        this.id = seasonCi.getId();
        this.names =
            locales
                .stream()
                .filter(l -> seasonCi.getName(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, seasonCi::getName));
        this.startDate = seasonCi.getStartDate();
        this.endDate = seasonCi.getEndDate();
        this.year = seasonCi.getYear();
        this.tournamentId = seasonCi.getTournamentId();
    }

    /**
     * Returns a {@link Urn} uniquely identifying the current {@link SeasonInfo} instance
     *
     * @return - a {@link Urn} uniquely identifying the current {@link SeasonInfo} instance
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns the name of the season in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the season in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return names.get(locale);
    }

    /**
     * Returns an unmodifiable {@link Map} containing translated names
     * @see com.google.common.collect.ImmutableMap
     *
     * @return - an unmodifiable {@link Map} containing translated names
     */
    @Override
    public Map<Locale, String> getNames() {
        return names;
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
     * Returns a {@link String} describing the current {@link SeasonInfo} instance
     *
     * @return - a {@link String} describing the current {@link SeasonInfo} instance
     */
    @Override
    public String toString() {
        return (
            "SeasonInfoImpl{" +
            "id=" +
            id +
            ", names=" +
            names +
            ", startDate=" +
            startDate +
            ", endDate=" +
            endDate +
            ", year=" +
            year +
            ", tournamentId=" +
            tournamentId +
            '}'
        );
    }
}
