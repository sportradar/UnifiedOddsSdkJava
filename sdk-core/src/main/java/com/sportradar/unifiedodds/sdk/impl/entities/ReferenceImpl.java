/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import java.util.Map;

/**
 * A representation of the various references
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "UnnecessaryParentheses" })
public class ReferenceImpl implements Reference {

    /**
     * The Betradar id for this instance
     */
    private final Integer betradarId;

    /**
     * The Betfair id for this instance
     */
    private final Integer betfairId;

    /**
     * The rotation number for this instance
     */
    private final Integer rotationNumber;

    /**
     * The AAMS id for this instance
     */
    private final Integer aamsId;

    /**
     * All the reference ids associated with the current instance
     *
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<String, String> references;

    /**
     * Initializes a new instance of {@link ReferenceImpl}
     *
     * @param referenceIdCI - a {@link ReferenceIdCI} that is used to construct the new instance
     */
    ReferenceImpl(ReferenceIdCI referenceIdCI) {
        Preconditions.checkNotNull(referenceIdCI);

        this.betfairId = referenceIdCI.getBetfairId();
        this.betradarId = referenceIdCI.getBetradarId();
        this.rotationNumber = referenceIdCI.getRotationNumber();
        this.aamsId = referenceIdCI.getAamsId();
        this.references = ImmutableMap.copyOf(referenceIdCI.getReferenceIds());
    }

    /**
     * Returns the Betradar id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Betradar id for this instance if provided amount reference ids, null otherwise
     */
    @Override
    public Integer getBetradarId() {
        return betradarId;
    }

    /**
     * Returns the Betfair id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Betfair id for this instance if provided amount reference ids, null otherwise
     */
    @Override
    public Integer getBetfairId() {
        return betfairId;
    }

    /**
     * Returns the rotation number for this instance if provided amount reference ids, null otherwise
     *
     * @return - the rotation number for this instance if provided amount reference ids, null otherwise
     */
    @Override
    public Integer getRotationNumber() {
        return rotationNumber;
    }

    /**
     * Returns the AAMS id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the AAMS id for this instance if provided amount reference ids, null otherwise
     */
    @Override
    public Integer getAamsId() {
        return aamsId;
    }

    /**
     * Returns a {@link Map} with all the reference ids associated with the current instance
     *
     * @return - all the reference ids associated with the current instance
     * @see com.google.common.collect.ImmutableMap
     */
    @Override
    public Map<String, String> getReferences() {
        return references;
    }

    /**
     * Returns a {@link String} describing the current {@link Reference} instance
     *
     * @return - a {@link String} describing the current {@link Reference} instance
     */
    @Override
    public String toString() {
        return (
            "ReferenceImpl{" +
            "betradarId=" +
            betradarId +
            ", betfairId=" +
            betfairId +
            ", rotationNumber=" +
            rotationNumber +
            ", references=" +
            references +
            '}'
        );
    }
}
