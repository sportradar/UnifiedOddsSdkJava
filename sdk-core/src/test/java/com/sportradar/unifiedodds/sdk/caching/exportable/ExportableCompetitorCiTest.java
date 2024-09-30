/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.exportable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class ExportableCompetitorCiTest {

    @Test
    public void shouldCreateWithDivision() {
        final Integer divisionId = 1;
        final String divisionName = "FBS";
        ExportableDivisionCi division = new ExportableDivisionCi(divisionId, divisionName);
        ExportableCompetitorCi competitor = createWithDivision(division);

        ExportableDivisionCi actualDivision = competitor.getDivision();
        assertEquals(division, actualDivision);
    }

    @Test
    public void shouldCreateWithNullDivision() {
        ExportableCompetitorCi competitor = createWithDivision(null);
        ExportableDivisionCi actualDivision = competitor.getDivision();
        assertEquals(null, actualDivision);
    }

    private ExportableCompetitorCi createWithDivision(ExportableDivisionCi division) {
        return new ExportableCompetitorCi(
            "sr:competitor:1",
            new HashMap<>(),
            Locale.ENGLISH,
            new HashMap<>(),
            new HashMap<>(),
            false,
            "no",
            new HashMap<>(),
            new ArrayList<>(),
            new HashMap<>(),
            new ArrayList<>(),
            mock(ExportableManagerCi.class),
            mock(ExportableVenueCi.class),
            "",
            "",
            mock(ExportableRaceDriverProfileCi.class),
            new ArrayList<>(),
            "",
            "",
            "",
            "",
            division
        );
    }
}
