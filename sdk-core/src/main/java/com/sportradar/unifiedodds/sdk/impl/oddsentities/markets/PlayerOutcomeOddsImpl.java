/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFOutcomeActive;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.TeamCompetitor;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.AdditionalProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.PlayerOutcomeOdds;

import java.util.Locale;

/**
 * Describes a player outcome. A player outcome is an outcome that is related to a player profile.
 */
public class PlayerOutcomeOddsImpl extends OutcomeOddsImpl implements PlayerOutcomeOdds {
    /**
     * The associated match instance
     */
    private final Match match;

    /**
     * An indication if the associated team is home or away
     */
    private final Integer teamIndication;

    PlayerOutcomeOddsImpl(String id,
                          NameProvider nameProvider,
                          OutcomeDefinition outcomeDefinition,
                          Locale defaultLocale,
                          UFOutcomeActive active,
                          Double odds,
                          Double probability,
                          Match match,
                          Integer teamIndication,
                          AdditionalProbabilities additionalProbabilities) {
        super(id, nameProvider, outcomeDefinition, defaultLocale, active, odds, probability, additionalProbabilities);

        Preconditions.checkNotNull(match);
        Preconditions.checkNotNull(teamIndication);
        Preconditions.checkArgument(teamIndication == 1 || teamIndication == 2);

        this.match = match;
        this.teamIndication = teamIndication;
    }

    /**
     * Indicates if the associated team is home or away
     *
     * @return an indication if the associated team is home or away
     */
    @Override
    public HomeAway getHomeOrAwayTeam() {
        return teamIndication == 1 ? HomeAway.Home : HomeAway.Away;
    }

    /**
     * Returns the associated team competitor
     *
     * @return the associated team competitor
     */
    @Override
    public TeamCompetitor getTeam() {
        switch (getHomeOrAwayTeam()) {
            case Home:
                return match.getHomeCompetitor();
            case Away:
                return match.getAwayCompetitor();
            default:
                return null;
        }
    }

    /**
     * Indicates if the outcome is {@link PlayerOutcomeOdds} instance
     *
     * @return <code>true</code> if the current outcome is a player outcome, otherwise <code>false</code>
     */
    @Override
    public boolean isPlayerOutcome() {
        return true;
    }
}
