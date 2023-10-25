/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableDivisionCi;

public class DivisionCi {

    private Integer division;
    private String divisionName;

    public DivisionCi(Integer division, String divisionName) {
        Preconditions.checkNotNull(division);
        this.division = division;
        this.divisionName = divisionName;
    }

    public DivisionCi(ExportableDivisionCi exportableDivisionCi) {
        this.division = exportableDivisionCi.getDivision();
        this.divisionName = exportableDivisionCi.getDivisionName();
    }

    public Integer getDivision() {
        return division;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public ExportableDivisionCi export() {
        return new ExportableDivisionCi(division, divisionName);
    }
}
