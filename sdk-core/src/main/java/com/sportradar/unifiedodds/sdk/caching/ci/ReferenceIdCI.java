/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

/**
 * The reference id representation used by caching components
 */
@SuppressWarnings(
    {
        "AbbreviationAsWordInName",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "HiddenField",
        "MethodLength",
        "NPathComplexity",
        "NestedIfDepth",
    }
)
public class ReferenceIdCI {

    /**
     * The betradarId property backing field
     */
    private Integer betradarId;

    /**
     * The betfairId property backing field
     */
    private Integer betfairId;

    /**
     * The rotation number for this instance
     */
    private Integer rotationNumber;

    /**
     * The AAMS id for this instance
     */
    private Integer aamsId;

    /**
     * A {@link ImmutableMap} containing all the referenceIds
     */
    private ImmutableMap<String, String> referenceIds;

    /**
     * Initializes a new instance of the {@link ReferenceIdCI} class
     *
     * @param newReferenceIds - A {@link Map} with the available references
     */
    public ReferenceIdCI(Map<String, String> newReferenceIds) {
        if (newReferenceIds != null) {
            merge(newReferenceIds);
        } else {
            this.betradarId = null;
            this.betfairId = null;
            this.rotationNumber = null;
            this.referenceIds = null;
        }
    }

    public void merge(Map<String, String> newReferenceIds) {
        if (newReferenceIds != null) {
            if (this.referenceIds == null) {
                this.referenceIds = ImmutableMap.copyOf(newReferenceIds);
            } else {
                Map<String, String> refs = new HashMap<>(this.referenceIds);
                for (Map.Entry<String, String> e : newReferenceIds.entrySet()) {
                    if (!refs.containsKey(e.getKey())) {
                        refs.put(e.getKey(), e.getValue());
                    }
                }
                this.referenceIds = ImmutableMap.copyOf(refs);
            }

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

            Integer aamsId;
            try {
                if (this.referenceIds.containsKey("aams")) {
                    aamsId = Integer.parseInt(this.referenceIds.get("aams"));
                } else {
                    aamsId = null;
                }
            } catch (NumberFormatException e) {
                aamsId = null;
            }
            this.aamsId = aamsId;
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
    public Integer getRotationNumber() {
        return rotationNumber;
    }

    /**
     * Returns the AAMS id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the AAMS id for this instance if provided amount reference ids, null otherwise
     */
    public Integer getAamsId() {
        return aamsId;
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
