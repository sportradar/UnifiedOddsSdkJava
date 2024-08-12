/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.exportable;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

public class ExportableDivisionCiTest {

    private final Integer anyDivision = 1;
    private final String anyDivisionName = "FBS";

    @Test
    public void shouldCreateWithDivisionAndName() {
        ExportableDivisionCi exportableDivisionCi = new ExportableDivisionCi(anyDivision, anyDivisionName);
        assertDivisionValues(exportableDivisionCi, anyDivision, anyDivisionName);
    }

    private void assertDivisionValues(
        ExportableDivisionCi exportableDivisionCi,
        Integer expectedDivision,
        String expectedDivisionName
    ) {
        assertEquals(expectedDivision, exportableDivisionCi.getDivision());
        assertEquals(expectedDivisionName, exportableDivisionCi.getDivisionName());
    }
}
