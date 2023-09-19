/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes providing basic tournament round information
 */
@SuppressWarnings({ "OverloadMethodsDeclarationOrder" })
public interface Round {
    /**
     * Returns the type of the round
     *
     * @return - the type of the round
     */
    String getType();

    /**
     * Returns the value specifying the round number or a null reference if round number is not defined
     *
     * @return - the value specifying the round number or a null reference if round number is not defined
     */
    Integer getNumber();

    /**
     * Returns the round instance name in the requested locale
     *
     * @param locale - a {@link Locale} in which the name is requested
     * @return - the round instance name translated in the requested locale
     */
    String getName(Locale locale);

    /**
     * Returns an unmodifiable {@link Map} which contains translated {@link Round} names
     *
     * @return - an unmodifiable {@link Map} which contains translated {@link Round} names
     */
    Map<Locale, String> getNames();

    /**
     * Returns the name of the group associated with the current round
     *
     * @return - the name of the group associated with the current round
     */
    String getGroupName();

    /**
     * Returns the id of the other match
     *
     * @return - the id of the other match
     */
    String getOtherMatchId();

    /**
     * Returns a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current {@link Round} instance
     *
     * @return - a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current {@link Round} instance
     */
    Integer getCupRoundMatches();

    /**
     * Returns a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current {@link Round} instance
     *
     * @return - a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current {@link Round} instance
     */
    Integer getCupRoundMatchNumber();

    /**
     * Returns the Betradar identifier
     *
     * @return - the Betradar identifier; or null if unavailable
     */
    Integer getBetradarId();

    /**
     * Returns the name or group long name for the specified locale
     *
     * @param locale {@link Locale} specifying the language of the value
     * @return the name or group long name if exists, or null
     */
    String getPhaseOrGroupLongName(Locale locale);

    /**
     * Returns the id of the group associated with the current round
     *
     * @return - the id of the group associated with the current round
     */
    default Urn getGroupId() {
        return null;
    }

    /**
     * Returns the phase of the round
     * @return the phase of the round
     */
    default String getPhase() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the group name of the group associated with the current round
     *
     * @return - the group name of the group associated with the current round
     */
    default String getGroupName(Locale locale) {
        return getGroupName();
    }

    /**
     * Returns the group name of the group associated with the current round
     *
     * @return - the group name of the group associated with the current round
     */
    default String getGroup() {
        return null;
    }

    /**
     * Returns the betradar name
     *
     * @return - the betradar name
     */
    default String getBetradarName() {
        return null;
    }
}
