/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.Division;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.DivisionCi;

public class DivisionImpl implements Division {

    private Integer division;
    private String divisionName;

    public DivisionImpl(DivisionCi divisionCi) {
        Preconditions.checkNotNull(divisionCi);
        this.division = divisionCi.getDivision();
        this.divisionName = divisionCi.getDivisionName();
    }

    public Integer getDivision() {
        return division;
    }

    public String getDivisionName() {
        return divisionName;
    }
}
