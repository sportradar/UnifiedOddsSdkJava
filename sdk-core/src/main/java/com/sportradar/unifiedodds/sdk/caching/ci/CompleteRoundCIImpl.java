/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchRound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A round representation used by caching components. The cache item exists as a whole object,
 * there is no support for partial loading
 */
public class CompleteRoundCIImpl implements CompleteRoundCI {
    /**
     * A {@link Map} containing round names in different languages
     */
    private final Map<Locale, String> names;

    /**
     * A {@link Map} containing phase or group name in different languages
     */
    private final Map<Locale, String> phaseOrGroupLongNames;

    /**
     * Type of the round
     */
    private String type;

    /**
     * The name of the group associated with the current round
     */
    private String group;

    /**
     * The id of the other match
     */
    private String otherMatchId;

    /**
     * A value specifying the round number
     */
    private Integer number;

    /**
     * A value specifying the number of matches in the current cup round
     */
    private Integer cupRoundMatches;

    /**
     * A value specifying the number of the match in the current cup round
     */
    private Integer cupRoundMatchNumber;

    /**
     * The betradar identifier
     */
    private Integer betradarId;

    private final List<Locale> cachedLocales;

    /**
     * Initializes a new instance of the {@link CompleteRoundCIImpl} class
     *
     * @param round - {@link SAPIMatchRound} containing information about the round
     * @param locale - {@link Locale} specifying the language of the <i>round</i>
     */
    public CompleteRoundCIImpl(SAPIMatchRound round, Locale locale) {
        Preconditions.checkNotNull(round);
        Preconditions.checkNotNull(locale);

        names = Maps.newConcurrentMap();
        phaseOrGroupLongNames = Maps.newConcurrentMap();
        cachedLocales = Collections.synchronizedList(new ArrayList<>());

        merge(round, locale);
    }

    /**
     * Merges the information from the provided {@link SAPIMatchRound} into the current instance
     *
     * @param round - {@link SAPIMatchRound} containing information about the round
     * @param locale - {@link Locale} specifying the language of the <i>round</i>
     */
    @Override
    public void merge(SAPIMatchRound round, Locale locale) {
        Preconditions.checkNotNull(round);
        Preconditions.checkNotNull(locale);

        type = round.getType();
        group = round.getGroup();
        otherMatchId = round.getOtherMatchId();
        number = round.getNumber();
        cupRoundMatches = round.getCupRoundMatches();
        cupRoundMatchNumber = round.getCupRoundMatchNumber();
        betradarId = round.getBetradarId();

        if (round.getName() != null) {
            names.put(locale, round.getName());
        }

        if (round.getGroupLongName() != null) {
            phaseOrGroupLongNames.put(locale, round.getGroupLongName());
        }
    }

    /**
     * Returns the type of the round
     *
     * @return - the type of the round
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Returns the name of the group associated with the current round
     *
     * @return - the name of the group associated with the current round
     */
    @Override
    public String getGroup() {
        return group;
    }

    /**
     * Returns the id of the other match
     *
     * @return - the id of the other match
     */
    @Override
    public String getOtherMatchId() {
        return otherMatchId;
    }

    /**
     * Returns a value specifying the round number or a null reference if round number is not defined
     *
     * @return - a value specifying the round number or a null reference if round number is not defined
     */
    @Override
    public Integer getNumber() {
        return number;
    }

    /**
     * Returns a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     *
     * @return - a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     */
    @Override
    public Integer getCupRoundMatches() {
        return cupRoundMatches;
    }

    /**
     * Returns a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     *
     * @return - a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     */
    @Override
    public Integer getCupRoundMatchNumber() {
        return cupRoundMatchNumber;
    }

    /**
     * Returns the betradar identifier
     *
     * @return - the betradar identifier; or null if unavailable
     */
    @Override
    public Integer getBetradarId() {
        return betradarId;
    }

    /**
     * Returns the name for specific locale
     *
     * @param locale - {@link Locale} specifying the language of the returned nationality
     * @return - Return the name if exists, or null
     */
    @Override
    public String getName(Locale locale) {
        return names.getOrDefault(locale, null);
    }

    /**
     * Returns the name or group long name for the specified locale
     *
     * @param locale {@link Locale} specifying the language of the value
     * @return the name or group long name if exists, or null
     */
    @Override
    public String getPhaseOrGroupLongName(Locale locale) {
        return phaseOrGroupLongNames.getOrDefault(locale, null);
    }

    /**
     * Checks if the associated cache item contains all the provided {@link Locale}s
     *
     * @param locales the {@link Locale}s that should be checked
     * @return <code>true</code> if all the provided {@link Locale}s are cached, otherwise <code>false</code>
     */
    @Override
    public boolean hasTranslationsFor(List<Locale> locales) {
        return cachedLocales.containsAll(locales);
    }

    @Override
    public String toString() {
        return "CompleteRoundCIImpl{" +
                "names=" + names +
                ", phaseOrGroupLongNames=" + phaseOrGroupLongNames +
                ", type='" + type + '\'' +
                ", group='" + group + '\'' +
                ", otherMatchId='" + otherMatchId + '\'' +
                ", number=" + number +
                ", cupRoundMatches=" + cupRoundMatches +
                ", cupRoundMatchNumber=" + cupRoundMatchNumber +
                ", betradarId=" + betradarId +
                ", cachedLocales=" + cachedLocales +
                '}';
    }
}
