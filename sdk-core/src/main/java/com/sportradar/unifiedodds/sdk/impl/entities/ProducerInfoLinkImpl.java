/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.entities.ProducerInfoLink;

/**
 * Represents a producer info link
 */
public class ProducerInfoLinkImpl implements ProducerInfoLink {
    /**
     * The reference to the producer info represented by the current instance
     */
    private final String reference;

    /**
     * The name of the producer link represented by the current instance
     */
    private final String name;


    /**
     * Initializes a new instance of the {@link ProducerInfoLink} class
     *
     * @param reference - the reference to the producer info represented by the current instance
     * @param name - the name of the producer link represented by the current instance
     */
    public ProducerInfoLinkImpl(String reference, String name) {
        this.reference = reference;
        this.name = name;
    }


    /**
     * Returns the reference to the producer info represented by the current instance
     *
     * @return - the reference to the producer info represented by the current instance
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Returns the name of the producer link represented by the current instance
     *
     * @return - the name of the producer link represented by the current instance
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link String} describing the current {@link ProducerInfoLink} instance
     *
     * @return - a {@link String} describing the current {@link ProducerInfoLink} instance
     */
    @Override
    public String toString() {
        return "ProducerInfoLinkImpl{" +
                "reference='" + reference + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
