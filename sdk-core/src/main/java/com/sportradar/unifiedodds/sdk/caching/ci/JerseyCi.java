/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiJersey;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableJerseyCi;

/**
 * A cache representation of a jersey
 */
public class JerseyCi {

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
     * The jersey has stripes
     */
    private final Boolean stripes;

    /**
     * The jersey has horizontal stripes
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
     * The jersey squares color
     */
    private final String squaresColor;

    /**
     * The jersey has split
     */
    private final Boolean split;

    /**
     * Initializes a new {@link JerseyCi}
     *
     * @param jersey the API schema object which will be used to construct the instance
     */
    public JerseyCi(SapiJersey jersey) {
        Preconditions.checkNotNull(jersey);

        base = jersey.getBase();
        number = jersey.getNumber();
        sleeve = jersey.getSleeve();
        type = jersey.getType();
        stripesColor = jersey.getStripesColor();
        splitColor = jersey.getSplitColor();
        shirtType = jersey.getShirtType();
        sleeveDetail = jersey.getSleeveDetail();
        stripes = jersey.isStripes();
        horizontalStripes = jersey.isHorizontalStripes();
        horizontalStripesColor = jersey.getHorizontalStripesColor();
        squares = jersey.isSquares();
        squaresColor = jersey.getSquaresColor();
        split = jersey.isSplit();
    }

    public JerseyCi(ExportableJerseyCi exportable) {
        Preconditions.checkNotNull(exportable);

        base = exportable.getBase();
        number = exportable.getNumber();
        sleeve = exportable.getSleeve();
        type = exportable.getType();
        stripesColor = exportable.getStripesColor();
        splitColor = exportable.getSplitColor();
        shirtType = exportable.getShirtType();
        sleeveDetail = exportable.getSleeveDetail();
        stripes = exportable.getStripes();
        horizontalStripes = exportable.getHorizontalStripes();
        horizontalStripesColor = exportable.getHorizontalStripesColor();
        squares = exportable.getSquares();
        squaresColor = exportable.getSquaresColor();
        split = exportable.getSplit();
    }

    /**
     * Returns the base color of the jersey
     *
     * @return the base color of the jersey
     */
    public String getBase() {
        return base;
    }

    /**
     * Returns the jersey number color
     *
     * @return the jersey number color
     */
    public String getNumber() {
        return number;
    }

    /**
     * Returns the sleeve color of the jersey
     *
     * @return the sleeve color of the jersey
     */
    public String getSleeve() {
        return sleeve;
    }

    /**
     * Returns the jersey type
     *
     * @return the jersey type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the jersey stripes color
     *
     * @return the jersey stripes color
     */
    public String getStripesColor() {
        return stripesColor;
    }

    /**
     * Returns the jersey split color
     *
     * @return the jersey split color
     */
    public String getSplitColor() {
        return splitColor;
    }

    /**
     * Returns the jersey shirt type
     *
     * @return the jersey shirt type
     */
    public String getShirtType() {
        return shirtType;
    }

    /**
     * Returns the jersey sleeve detail
     *
     * @return the jersey sleeve detail
     */
    public String getSleeveDetail() {
        return sleeveDetail;
    }

    /**
     * Returns the jersey stripes
     *
     * @return the jersey stripes
     */
    public Boolean getStripes() {
        return stripes;
    }

    /**
     * Returns the jersey horizontal stripes
     *
     * @return the jersey horizontal stripes
     */
    public Boolean getHorizontalStripes() {
        return horizontalStripes;
    }

    /**
     * Returns the jersey horizontal stripes color
     *
     * @return the jersey horizontal stripes color
     */
    public String getHorizontalStripesColor() {
        return horizontalStripesColor;
    }

    /**
     * returns information about the jersey squares
     *
     * @return information about the jersey squares
     */
    public Boolean getSquares() {
        return squares;
    }

    /**
     * returns the jersey squares color
     *
     * @return the jersey squares color
     */
    public String getSquaresColor() {
        return squaresColor;
    }

    /**
     * Returns the jersey split
     *
     * @return the jersey split
     */
    public Boolean getSplit() {
        return split;
    }

    public ExportableJerseyCi export() {
        return new ExportableJerseyCi(
            base,
            number,
            sleeve,
            type,
            stripesColor,
            splitColor,
            shirtType,
            sleeveDetail,
            stripes,
            horizontalStripes,
            horizontalStripesColor,
            squares,
            squaresColor,
            split
        );
    }
}
