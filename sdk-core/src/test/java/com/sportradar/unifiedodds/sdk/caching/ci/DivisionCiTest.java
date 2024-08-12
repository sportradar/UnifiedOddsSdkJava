/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableDivisionCi;
import org.junit.jupiter.api.Test;

public class DivisionCiTest {

    private final Integer division = 1;
    private final String divisionName = "FBS";

    @Test
    public void shouldCreateWithDivision() {
        DivisionCi divisionCi = new DivisionCi(division, null);
        assertEquals(division, divisionCi.getDivision());
    }

    @Test
    public void shouldCreateWithDivisionName() {
        DivisionCi divisionCi = new DivisionCi(division, divisionName);
        assertEquals(divisionName, divisionCi.getDivisionName());
    }

    @Test
    public void doesNotCreateWithNullDivision() {
        assertThatThrownBy(() -> {
                new DivisionCi(null, "anyName");
            })
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldExportWithDivisionAndName() {
        DivisionCi divisionCi = new DivisionCi(division, divisionName);
        assertExportableDivisionValues(divisionCi.export(), division, divisionName);
    }

    @Test
    public void shouldCreateFromExportableCi() {
        DivisionCi divisionCi = new DivisionCi(division, divisionName);
        ExportableDivisionCi exportableDivisionCi = divisionCi.export();
        DivisionCi divisionFromExportable = new DivisionCi(exportableDivisionCi);
        assertDivisionValues(divisionFromExportable, division, divisionName);
    }

    private void assertDivisionValues(
        DivisionCi divisionCi,
        Integer expectedDivision,
        String expectedDivisionName
    ) {
        assertEquals(expectedDivision, divisionCi.getDivision());
        assertEquals(expectedDivisionName, divisionCi.getDivisionName());
    }

    private void assertExportableDivisionValues(
        ExportableDivisionCi exportableDivision,
        Integer expectedDivision,
        String expectedDivisionName
    ) {
        assertEquals(expectedDivision, exportableDivision.getDivision());
        assertEquals(expectedDivisionName, exportableDivision.getDivisionName());
    }
}
