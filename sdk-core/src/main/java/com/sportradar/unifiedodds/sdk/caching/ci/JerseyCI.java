/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIJersey;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableJerseyCI;

/**
 * A cache representation of a jersey
 */
public class JerseyCI {

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
     * Initializes a new {@link JerseyCI}
     *
     * @param jersey the API schema object which will be used to construct the instance
     */
    public JerseyCI(SAPIJersey jersey) {
        Preconditions.checkNotNull(jersey);

        base = jersey.getBase();
        number = jersey.getNumber();
        sleeve = jersey.getSleeve();
        type = jersey.getType();
        stripesColor = jersey.getStripesColor();
        splitColor = jersey.getSplitColor();
        shirtType = jersey.getShirtType();
        sleeveDetail = jersey.getSleeveDetail();
    }

    public JerseyCI(ExportableJerseyCI exportable) {
        Preconditions.checkNotNull(exportable);

        base = exportable.getBase();
        number = exportable.getNumber();
        sleeve = exportable.getSleeve();
        type = exportable.getType();
        stripesColor = exportable.getStripesColor();
        splitColor = exportable.getSplitColor();
        shirtType = exportable.getShirtType();
        sleeveDetail = exportable.getSleeveDetail();
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

    public ExportableJerseyCI export() {
        return new ExportableJerseyCI(
                base,
                number,
                sleeve,
                type,
                stripesColor,
                splitColor,
                shirtType,
                sleeveDetail
        );
    }
}
