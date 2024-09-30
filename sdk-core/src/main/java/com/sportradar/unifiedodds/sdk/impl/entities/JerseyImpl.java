/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.JerseyCi;
import com.sportradar.unifiedodds.sdk.entities.Jersey;

/**
 * A base implementation describing a jersey
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class JerseyImpl implements Jersey {

    /**
     * The jersey base color
     */
    private final String base;

    /**
     * The jersey number
     */
    private final String number;

    /**
     * The jersey sleeve color
     */
    private final String sleeve;

    /**
     * The jersey type
     */
    private final String type;

    /**
     * The jersey stripes color
     */
    private final String stripesColor;

    /**
     * The jersey split color
     */
    private final String splitColor;

    /**
     * The jersey shirt type
     */
    private final String shirtType;

    /**
     * The jersey sleeve detail
     */
    private final String sleeveDetail;

    /**
     * The jersey stripes
     */
    private final Boolean stripes;

    /**
     * The jersey horizontal stripes
     */
    private final Boolean horizontalStripes;

    /**
     * The jersey horizontal stripes color
     */
    private final String horizontalStripesColor;

    /**
     * The jersey has squares
     */
    private final Boolean squares;

    /**
     * The jersey has squares color
     */
    private final String squaresColor;

    /**
     * The jersey has split
     */
    private final Boolean split;

    /**
     * Initializes a new {@link JerseyImpl}
     *
     * @param jersey the CI which will be used to construct the instance
     */
    JerseyImpl(JerseyCi jersey) {
        Preconditions.checkNotNull(jersey);

        base = jersey.getBase();
        number = jersey.getNumber();
        sleeve = jersey.getSleeve();
        type = jersey.getType();
        stripesColor = jersey.getStripesColor();
        splitColor = jersey.getSplitColor();
        shirtType = jersey.getShirtType();
        sleeveDetail = jersey.getSleeveDetail();
        stripes = jersey.getStripes();
        horizontalStripes = jersey.getHorizontalStripes();
        horizontalStripesColor = jersey.getHorizontalStripesColor();
        squares = jersey.getSquares();
        squaresColor = jersey.getSquaresColor();
        split = jersey.getSplit();
    }

    /**
     * Returns the base color of the jersey
     *
     * @return the base color of the jersey
     */
    @Override
    public String getBase() {
        return base;
    }

    /**
     * Returns the jersey number color
     *
     * @return the jersey number color
     */
    @Override
    public String getNumber() {
        return number;
    }

    /**
     * Returns the sleeve color of the jersey
     *
     * @return the sleeve color of the jersey
     */
    @Override
    public String getSleeve() {
        return sleeve;
    }

    /**
     * Returns the jersey type
     *
     * @return the jersey type
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Returns the jersey stripes color
     *
     * @return the jersey stripes color
     */
    @Override
    public String getStripesColor() {
        return stripesColor;
    }

    /**
     * Returns the jersey split color
     *
     * @return the jersey split color
     */
    @Override
    public String getSplitColor() {
        return splitColor;
    }

    /**
     * Returns the jersey shirt type
     *
     * @return the jersey shirt type
     */
    @Override
    public String getShirtType() {
        return shirtType;
    }

    /**
     * Returns the jersey sleeve detail
     *
     * @return the jersey sleeve detail
     */
    @Override
    public String getSleeveDetail() {
        return sleeveDetail;
    }

    /**
     * Returns the jersey has stripes
     *
     * @return the jersey has stripes
     */
    @Override
    public Boolean getStripes() {
        return stripes;
    }

    /**
     * Returns the jersey has horizontal stripes
     *
     * @return the jersey has horizontal stripes
     */
    @Override
    public Boolean getHorizontalStripes() {
        return horizontalStripes;
    }

    /**
     * Returns the jersey horizontal stripes color
     *
     * @return the jersey horizontal stripes color
     */
    @Override
    public String getHorizontalStripesColor() {
        return horizontalStripesColor;
    }

    /**
     * returns information about the jersey squares
     *
     * @return information about the jersey squares
     */
    @Override
    public Boolean getSquares() {
        return squares;
    }

    /**
     * returns information about the jersey squares
     *
     * @return information about the jersey squares
     */
    @Override
    public String getSquaresColor() {
        return squaresColor;
    }

    /**
     * returns information about the jersey split
     *
     * @return information about the jersey split
     */
    @Override
    public Boolean getSplit() {
        return split;
    }

    @Override
    public String toString() {
        return (
            "JerseyImpl{" +
            "base='" +
            base +
            '\'' +
            ", number='" +
            number +
            '\'' +
            ", sleeve='" +
            sleeve +
            '\'' +
            ", type='" +
            type +
            '\'' +
            ", stripesColor='" +
            stripesColor +
            '\'' +
            ", splitColor='" +
            splitColor +
            '\'' +
            ", shirtType='" +
            shirtType +
            '\'' +
            ", sleeveDetail='" +
            sleeveDetail +
            '\'' +
            ", stripes='" +
            stripes +
            '\'' +
            ", horizontalStripes='" +
            horizontalStripes +
            '\'' +
            ", horizontalStripesColor='" +
            horizontalStripesColor +
            '\'' +
            ", squares='" +
            squares +
            '\'' +
            ", squaresColor='" +
            squaresColor +
            '\'' +
            ", split='" +
            split +
            '\'' +
            '}'
        );
    }
}
