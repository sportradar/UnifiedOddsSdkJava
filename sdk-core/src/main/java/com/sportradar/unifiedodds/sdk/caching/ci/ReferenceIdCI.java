/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * The reference id representation used by caching components
 */
public class ReferenceIdCI {
    /**
     * The betradarId property backing field
     */
    private final Integer betradarId;

    /**
     * The betfairId property backing field
     */
    private final Integer betfairId;

    /**
     * The rotation number for this instance
     */
    private final Integer rotationNumber;

    /**
     * A {@link ImmutableMap} containing all the referenceIds
     */
    private final ImmutableMap<String, String> referenceIds;

    /**
     * Initializes a new instance of the {@link ReferenceIdCI} class
     *
     * @param referenceIds - A {@link Map} with the available references
     */
    public ReferenceIdCI(Map<String, String> referenceIds) {
        if (referenceIds != null) {
            this.referenceIds = ImmutableMap.copyOf(referenceIds);

            Integer betradarId;
            try {
                if (this.referenceIds.containsKey("betradar")) {
                    betradarId = Integer.parseInt(this.referenceIds.get("betradar"));
                } else if (this.referenceIds.containsKey("BetradarCtrl")) {
                    betradarId = Integer.parseInt(this.referenceIds.get("BetradarCtrl"));
                } else {
                    betradarId = null;
                }
            } catch (NumberFormatException e) {
                betradarId = null;
            }
            this.betradarId = betradarId;


            Integer betfairId;
            try {
                if (this.referenceIds.containsKey("betfair")) {
                    betfairId = Integer.parseInt(this.referenceIds.get("betfair"));
                } else if (this.referenceIds.containsKey("betFair")) {
                    betfairId = Integer.parseInt(this.referenceIds.get("betFair"));
                } else {
                    betfairId = null;
                }
            } catch (NumberFormatException e) {
                betfairId = null;
            }
            this.betfairId = betfairId;

            Integer rotationNbr;
            try {
                if (this.referenceIds.containsKey("rotation_number")) {
                    rotationNbr = Integer.parseInt(this.referenceIds.get("rotation_number"));
                } else {
                    rotationNbr = null;
                }
            } catch (NumberFormatException e) {
                rotationNbr = null;
            }
            this.rotationNumber = rotationNbr;

        } else {
            this.betradarId = null;
            this.betfairId = null;
            this.rotationNumber = null;
            this.referenceIds = null;
        }
    }

    /**
     * Returns the referenceId for Betradar if available, null otherwise.
     *
     * @return - the referenceId for Betradar if available, null otherwise.
     */
    public Integer getBetradarId() {
        return betradarId;
    }

    /**
     * Returns the referenceId for Betfair if available, null otherwise.
     *
     * @return - the referenceId for Betfair if available, null otherwise.
     */
    public Integer getBetfairId() {
        return betfairId;
    }

    /**
     * Returns the rotation number for this instance if provided amount reference ids, null otherwise
     *
     * @return - the rotation number for this instance if provided amount reference ids, null otherwise
     */
    public Integer getRotationNumber()  {
        return rotationNumber;
    }

    /**
     * Returns a {@link Map} of referenceIds who's content can't be changed
     * @see ImmutableMap
     *
     * @return - a {@link Map} of referenceIds who's content can't be changed
     */
    public Map<String, String> getReferenceIds() {
        return referenceIds;
    }
}
