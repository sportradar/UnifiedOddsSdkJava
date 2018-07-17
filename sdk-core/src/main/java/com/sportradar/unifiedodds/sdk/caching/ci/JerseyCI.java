/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIJersey;

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
}
