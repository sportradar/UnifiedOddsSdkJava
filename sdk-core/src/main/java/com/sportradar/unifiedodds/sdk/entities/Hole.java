package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods implemented by classes representing a hole
 */
public interface Hole {
    /**
     * Gets the number of the hole
     * @return the umber of the hole
     */
    public int getNumber();

    /**
     * Gets the par of the hole
     * @return the par of the hole
     */
    public int getPar();
}
