/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportAsDiIsNotTestedAtUnitTestLevel;

/**
 * Defines methods used to access jersey information
 */
@ExcludeFromJacocoGeneratedReportAsDiIsNotTestedAtUnitTestLevel
public interface Jersey {
    String METHOD_NOT_IMPLEMENTED = "Method not implemented. Use derived type.";

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

    /**
     * Indicated if the jersey has stripes
     *
     * @return the jersey has stripes
     */
    default Boolean getStripes() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLEMENTED);
    }

    /**
     * Indicated if the jersey has horizontal stripes
     *
     * @return the jersey has horizontal stripes
     */
    default Boolean getHorizontalStripes() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLEMENTED);
    }

    /**
     * returns the jersey horizontal stripes color
     *
     * @return the jersey horizontal stripes color
     */
    default String getHorizontalStripesColor() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLEMENTED);
    }

    /**
     * returns information about the jersey squares
     *
     * @return information about the jersey squares
     */
    default Boolean getSquares() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLEMENTED);
    }

    /**
     * returns the jersey squares color
     *
     * @return the jersey squares color
     */
    default String getSquaresColor() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLEMENTED);
    }

    /**
     * returns information about the jersey split
     *
     * @return information about the jersey split
     */
    default Boolean getSplit() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLEMENTED);
    }
}
