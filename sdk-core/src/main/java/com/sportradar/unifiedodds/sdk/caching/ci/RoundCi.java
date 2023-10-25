/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.sportradar.utils.Urn;
import java.util.Locale;

/**
 * A round representation used by caching components
 */
public interface RoundCi {
    /**
     * Returns the type of the round
     *
     * @return the type of the round
     */
    String getType();

    /**
     * Returns the name of the group associated with the current round
     *
     * @return the name of the group associated with the current round
     */
    String getGroup();

    /**
     * Returns the id of the group associated with the current round
     *
     * @return the id of the group associated with the current round
     */
    Urn getGroupId();

    /**
     * Returns the id of the other match
     *
     * @return the id of the other match
     */
    String getOtherMatchId();

    /**
     * Returns a value specifying the round number or a null reference if round number is not defined
     *
     * @return a value specifying the round number or a null reference if round number is not defined
     */
    Integer getNumber();

    /**
     * Returns a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     *
     * @return a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     */
    Integer getCupRoundMatches();

    /**
     * Returns a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     *
     * @return a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     */
    Integer getCupRoundMatchNumber();

    /**
     * Returns the betradar identifier
     *
     * @return the betradar identifier; or null if unavailable
     */
    Integer getBetradarId();

    /**
     * Returns the name for specific locale
     *
     * @param locale  {@link Locale} specifying the language of the returned name
     * @return Return the name if exists, or null
     */
    String getName(Locale locale);

    /**
     * Returns the name or group long name for the specified locale
     *
     * @param locale {@link Locale} specifying the language of the value
     * @return the name or group long name if exists, or null
     */
    String getPhaseOrGroupLongName(Locale locale);

    /**
     * Returns the phase of the round
     * @return the phase of the round
     */
    String getPhase();

    /**
     * Returns the betradar name
     * @return the betradar name
     */
    String getBetradarName();
}
