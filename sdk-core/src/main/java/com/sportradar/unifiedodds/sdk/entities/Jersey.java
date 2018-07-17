/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods used to access jersey information
 */
public interface Jersey {
    /**
     * Returns the base color of the jersey
     *
     * @return the base color of the jersey
     */
    String getBase();

    /**
     * Returns the jersey number color
     *
     * @return the jersey number color
     */
    String getNumber();

    /**
     * Returns the sleeve color of the jersey
     *
     * @return the sleeve color of the jersey
     */
    String getSleeve();

    /**
     * Returns the jersey type
     *
     * @return the jersey type
     */
    String getType();
}
