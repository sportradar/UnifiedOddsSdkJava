/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.RoundCi;
import com.sportradar.unifiedodds.sdk.entities.Round;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides basic tournament round information
 */
public class RoundImpl implements Round {

    private final RoundCi roundCi;
    private final List<Locale> locales;

    /**
     * Initializes a new instance of {@link RoundImpl}
     *
     * @param roundCi - a {@link RoundCi} used to make the instance
     * @param locales - a {@link List} of locales which are supported by the instance
     */
    RoundImpl(RoundCi roundCi, List<Locale> locales) {
        Preconditions.checkNotNull(roundCi);

        this.roundCi = roundCi;
        this.locales = locales;
    }

    /**
     * Returns the type of the round
     *
     * @return - the type of the round
     */
    @Override
    public String getType() {
        return roundCi.getType();
    }

    /**
     * Returns the value specifying the round number or a null reference if round number is not defined
     *
     * @return - the value specifying the round number or a null reference if round number is not defined
     */
    @Override
    public Integer getNumber() {
        return roundCi.getNumber();
    }

    /**
     * Returns the round instance name in the requested locale
     *
     * @param locale - a {@link Locale} in which the name is requested
     * @return - the round instance name translated in the requested locale
     */
    @Override
    public String getName(Locale locale) {
        if (!locales.contains(locale)) {
            return null;
        }
        return roundCi.getName(locale);
    }

    /**
     * Returns an unmodifiable {@link Map} which contains translated {@link Round} names
     * @see com.google.common.collect.ImmutableMap
     *
     * @return - an unmodifiable {@link Map} which contains translated {@link Round} names
     */
    @Override
    public Map<Locale, String> getNames() {
        return locales
            .stream()
            .filter(l -> roundCi.getName(l) != null)
            .collect(Collectors.toMap(k -> k, roundCi::getName));
    }

    /**
     * Returns the name or group long name for the specified locale
     *
     * @param locale {@link Locale} specifying the language of the value
     * @return the name or group long name if exists, or null
     */
    @Override
    public String getPhaseOrGroupLongName(Locale locale) {
        if (!locales.contains(locale)) {
            return null;
        }
        return roundCi.getPhaseOrGroupLongName(locale);
    }

    /**
     * Returns the id of the other match
     *
     * @return - the id of the other match
     */
    @Override
    public String getOtherMatchId() {
        return roundCi.getOtherMatchId();
    }

    /**
     * Returns a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current {@link Round} instance
     *
     * @return - a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current {@link Round} instance
     */
    @Override
    public Integer getCupRoundMatches() {
        return roundCi.getCupRoundMatches();
    }

    /**
     * Returns a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current {@link Round} instance
     *
     * @return - a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current {@link Round} instance
     */
    @Override
    public Integer getCupRoundMatchNumber() {
        return roundCi.getCupRoundMatchNumber();
    }

    /**
     * Returns the Betradar identifier
     *
     * @return - the Betradar identifier; or null if unavailable
     */
    @Override
    public Integer getBetradarId() {
        return roundCi.getBetradarId();
    }

    /**
     * Returns the id of the group associated with the current round
     *
     * @return - the id of the group associated with the current round
     */
    @Override
    public Urn getGroupId() {
        return roundCi.getGroupId();
    }

    /**
     * Returns the phase of the round
     * @return the phase of the round
     */
    @Override
    public String getPhase() {
        return roundCi.getPhase();
    }

    /**
     * Returns the group of the group associated with the current round
     *
     * @return - the group of the group associated with the current round
     */
    @Override
    public String getGroup() {
        return roundCi.getGroup();
    }

    /**
     * Returns the betradar name
     *
     * @return - the betradar name
     */
    @Override
    public String getBetradarName() {
        return roundCi.getBetradarName();
    }

    /**
     * Returns a {@link String} describing the current {@link Round} instance
     *
     * @return - a {@link String} describing the current {@link Round} instance
     */
    @Override
    public String toString() {
        return "RoundImpl{" + "roundCI=" + roundCi + ", locales=" + locales + '}';
    }
}
