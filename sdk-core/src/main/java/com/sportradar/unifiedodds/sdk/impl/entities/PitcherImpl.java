/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.caching.ci.PitcherCi;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.Pitcher;
import com.sportradar.unifiedodds.sdk.entities.PitcherHand;
import com.sportradar.unifiedodds.sdk.entities.Referee;
import com.sportradar.utils.Urn;

/**
 * Represents a sport event pitcher
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class PitcherImpl implements Pitcher {

    /**
     *  A value used to uniquely identify the current {@link Pitcher} instance
     */
    private final Urn id;

    /**
     * The name of the pitcher represented by the current {@link Pitcher} instance
     */
    private final String name;

    /**
     * Is home or away competitor
     */
    private HomeAway competitor;

    /**
     * Indication if the pitcher uses left or right hand
     */
    private PitcherHand hand;

    /**
     * Initializes a new instance of {@link PitcherImpl} class
     *
     * @param pitcherCi - a {@link PitcherCi} used to create a new instance
     */
    public PitcherImpl(PitcherCi pitcherCi) {
        this.id = pitcherCi.getId();
        this.name = pitcherCi.getName();
        this.competitor = pitcherCi.getCompetitor();
        this.hand = pitcherCi.getHand();
    }

    /**
     * Returns the unique identifier of the current {@link Referee} instance
     *
     * @return - the unique identifier of the current {@link Referee} instance
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns the name of the referee represented by the current {@link Referee} instance
     *
     * @return - the name of the referee represented by the current {@link Referee} instance
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns indication if the {@link Pitcher} is home or away
     *
     * @return - indication if the {@link Pitcher} is home or away
     */
    @Override
    public HomeAway getCompetitor() {
        return competitor;
    }

    /**
     * Returns indication if the {@link Pitcher} is left or right handed
     *
     * @return - indication if the {@link Pitcher} is left or right handed
     */
    @Override
    public PitcherHand getHand() {
        return hand;
    }

    @Override
    public String toString() {
        return (
            "RefereeImpl{" +
            "id=" +
            id +
            ", name='" +
            name +
            '\'' +
            ", competitor=" +
            competitor +
            ", hand=" +
            hand +
            '}'
        );
    }
}
