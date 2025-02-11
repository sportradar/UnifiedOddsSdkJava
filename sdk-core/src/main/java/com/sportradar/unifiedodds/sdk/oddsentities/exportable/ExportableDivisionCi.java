/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import com.google.common.base.Preconditions;
import java.io.Serializable;

public class ExportableDivisionCi implements Serializable {

    private final Integer division;
    private final String divisionName;

    public ExportableDivisionCi(Integer division, String divisionName) {
        this.division = division;
        this.divisionName = divisionName;
    }

    public Integer getDivision() {
        return division;
    }

    public String getDivisionName() {
        return divisionName;
    }
}
