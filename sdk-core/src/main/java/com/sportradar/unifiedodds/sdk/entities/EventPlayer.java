/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An interface providing methods to access player details for timeline event
 */
@SuppressWarnings({ "LineLength" })
public interface EventPlayer extends Player {
    /**
     * Returns the bench value
     * The bench value - in case of yellow or red card event, it is relevant to know if the player who is getting the card is sitting on the bench at that exact moment.
     * The attribute is equal to 1 if the player who gets the card is sitting on the bench. In case the player who gets the card is on the field, then the attribute is not added at all.
     * @return - the bench value
     */
    String getBench();
}
