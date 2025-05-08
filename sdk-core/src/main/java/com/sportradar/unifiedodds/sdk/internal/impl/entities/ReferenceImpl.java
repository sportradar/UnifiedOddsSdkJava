/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.ReferenceIdCi;
import java.util.Map;

/**
 * A representation of the various references
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
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
     * The Lugas id for this instance
     */
    private final String lugasId;

    /**
     * All the reference ids associated with the current instance
     *
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<String, String> references;

    /**
     * Initializes a new instance of {@link ReferenceImpl}
     *
     * @param referenceIdCi - a {@link ReferenceIdCi} that is used to construct the new instance
     */
    ReferenceImpl(ReferenceIdCi referenceIdCi) {
        Preconditions.checkNotNull(referenceIdCi);

        this.betfairId = referenceIdCi.getBetfairId();
        this.betradarId = referenceIdCi.getBetradarId();
        this.rotationNumber = referenceIdCi.getRotationNumber();
        this.aamsId = referenceIdCi.getAamsId();
        this.lugasId = referenceIdCi.getLugasId();
        this.references = ImmutableMap.copyOf(referenceIdCi.getReferenceIds());
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
     * Returns the Lugas id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Lugas id for this instance if provided amount reference ids, null otherwise
     */
    @Override
    public String getLugasId() {
        return lugasId;
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
