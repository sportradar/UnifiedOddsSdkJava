package com.sportradar.unifiedodds.sdk.impl;

/**
 * A contract implemented by the classes used to provide distinct values
 */
public interface SequenceGenerator {

    /**
     * Gets the next available distinct value
     * @return the next available distinct value
     */
    public int getNext();
}
