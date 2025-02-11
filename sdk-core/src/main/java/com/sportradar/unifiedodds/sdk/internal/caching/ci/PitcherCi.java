/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiPitcher;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.PitcherHand;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportablePitcherCi;
import com.sportradar.utils.Urn;
import java.util.Locale;

/**
 * A pitcher representation used by caching components
 */
public class PitcherCi extends SportEntityCi {

    /**
     * The name of the referee
     */
    private String name;

    /**
     * Is home or away competitor
     */
    private HomeAway competitor;

    /**
     * Indication if the pitcher uses left or right hand
     */
    private PitcherHand hand;

    /**
     * Initializes a new instance of the {@link PitcherCi} class
     *
     * @param pitcher - {@link SapiPitcher} containing information about the pitcher
     * @param locale  - {@link Locale} specifying the language of the <i>pitcher</i>
     */
    PitcherCi(SapiPitcher pitcher, Locale locale) {
        super(Urn.parse(pitcher.getId()));
        Preconditions.checkNotNull(pitcher);
        Preconditions.checkNotNull(locale);

        merge(pitcher, locale);
    }

    PitcherCi(ExportablePitcherCi exportable) {
        super(Urn.parse(exportable.getId()));
        name = exportable.getName();
        competitor = exportable.getCompetitor();
        hand = exportable.getHand();
    }

    /**
     * Merges the information from the provided {@link SapiPitcher} into the current instance
     *
     * @param pitcher - {@link SapiPitcher} containing information about the pitcher
     * @param locale  - {@link Locale} specifying the language of the <i>pitcher</i>
     */
    public void merge(SapiPitcher pitcher, Locale locale) {
        Preconditions.checkNotNull(pitcher);
        Preconditions.checkNotNull(locale);

        name = pitcher.getName();
        competitor = HomeAway.valueFromBasicStringDescription(pitcher.getCompetitor());
        hand = PitcherHand.valueFromBasicStringDescription(pitcher.getHand());
    }

    /**
     * Returns the name of the pitcher
     *
     * @return - the name of the pitcher
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the indication if the pitcher is home or away
     *
     * @return - the indication if the pitcher is home or away
     */
    public HomeAway getCompetitor() {
        return competitor;
    }

    /**
     * Returns which hand the pitcher uses
     *
     * @return - which hand the pitcher uses
     */
    public PitcherHand getHand() {
        return hand;
    }

    public ExportablePitcherCi export() {
        return new ExportablePitcherCi(getId().toString(), name, competitor, hand);
    }
}
