/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An interface providing methods to access producer info link properties
 */
public interface ProducerInfoLink {
    /**
     * Returns the reference to the producer info represented by the current instance
     *
     * @return - the reference to the producer info represented by the current instance
     */
    String getReference();

    /**
     * Returns the name of the producer link represented by the current instance
     *
     * @return - the name of the producer link represented by the current instance
     */
    String getName();
}
