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

    /**
     * Returns the jersey stripes color
     *
     * @return the jersey stripes color
     */
    default String getStripesColor() {
        return null;
    }

    /**
     * Returns the jersey split color
     *
     * @return the jersey split color
     */
    default String getSplitColor() {
        return null;
    }

    /**
     * Returns the jersey shirt type
     *
     * @return the jersey shirt type
     */
    default String getShirtType() {
        return null;
    }

    /**
     * Returns the jersey sleeve detail
     *
     * @return the jersey sleeve detail
     */
    default String getSleeveDetail() {
        return null;
    }

}
