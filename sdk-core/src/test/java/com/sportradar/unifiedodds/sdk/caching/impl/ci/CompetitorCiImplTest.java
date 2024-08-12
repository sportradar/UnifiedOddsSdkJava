/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamExtended;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.ci.DivisionCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCompetitorCiTest;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableDivisionCi;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.Urn;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Any;

public class CompetitorCiImplTest {

    private final Urn anyUrn = new Urn("sr", "competitor", 1);
    private final ExceptionHandlingStrategy exceptionHandlingStrategy = ExceptionHandlingStrategy.Catch;
    private final Locale anyLocale = Locale.ENGLISH;
    private final Integer anyDivision = 1;
    private final String anyDivisionName = "FBS";
    private final DataRouterManager dataRouterManager = mock(DataRouterManager.class);

    @Test
    public void shouldCreateWithDivisionAndDivisionName() {
        CompetitorCiImpl competitorCi = createFromSapiCompetitorEndpoint(
            createWithDivisionAndDivisionName(anyDivision, anyDivisionName)
        );
        assertDivisionInfo(competitorCi.getDivision(), anyDivision, anyDivisionName);
    }

    @Test
    public void shouldCreateWithoutDivisionName() {
        CompetitorCiImpl competitorCi = createFromSapiCompetitorEndpoint(
            createWithDivisionAndDivisionName(anyDivision, null)
        );
        assertDivisionInfo(competitorCi.getDivision(), anyDivision, null);
    }

    @Test
    public void shouldCreateWithoutDivision() {
        CompetitorCiImpl competitorCi = createFromSapiCompetitorEndpoint(
            createWithDivisionAndDivisionName(null, null)
        );
        DivisionCi actualDivisionCi = competitorCi.getDivision();
        assertEquals(actualDivisionCi, null);
    }

    @Test
    public void shouldExportDivision() {
        CompetitorCiImpl competitorCi = createFromSapiCompetitorEndpoint(
            createWithDivisionAndDivisionName(anyDivision, anyDivisionName)
        );
        ExportableCompetitorCi exportedCompetitor = (ExportableCompetitorCi) competitorCi.export();
        assertExportedDivisionValues(exportedCompetitor.getDivision(), anyDivision, anyDivisionName);
    }

    @Test
    public void shouldCreateWithDivisionFromExportableCi() {
        CompetitorCiImpl competitorCi = createFromSapiCompetitorEndpoint(
            createWithDivisionAndDivisionName(anyDivision, anyDivisionName)
        );
        ExportableCompetitorCi exportedCompetitor = (ExportableCompetitorCi) competitorCi.export();
        CompetitorCiImpl competitorFromExportable = new CompetitorCiImpl(
            exportedCompetitor,
            dataRouterManager,
            exceptionHandlingStrategy
        );
        DivisionCi actualDivision = competitorFromExportable.getDivision();
        assertDivisionInfo(actualDivision, anyDivision, anyDivisionName);
    }

    @Test
    public void divisionShouldBeExportedAsNull() {
        CompetitorCiImpl competitorCi = createFromSapiCompetitorEndpoint(
            createWithDivisionAndDivisionName(null, null)
        );
        ExportableCompetitorCi exportedCompetitor = (ExportableCompetitorCi) competitorCi.export();
        assertEquals(null, exportedCompetitor.getDivision());
    }

    @Test
    public void shouldFetchSummaryIfDivisionIsMissingAndSummaryHasNotBeenFetched()
        throws CommunicationException {
        CompetitorCiImpl competitorCi = createWithoutData();
        competitorCi.getDivision();
        verify(dataRouterManager, times(1)).requestCompetitorEndpoint(any(), any(), any());
    }

    @Test
    public void shouldNotFetchSummaryForDivisionIfSummaryAlreadyFetched() throws CommunicationException {
        CompetitorCiImpl competitorCi = createWithoutData();
        mockFetchedSummaryEndpointWithoutDivision(competitorCi);
        competitorCi.getDivision();
        verify(dataRouterManager, times(0)).requestCompetitorEndpoint(any(), any(), any());
    }

    private void assertDivisionInfo(
        DivisionCi division,
        Integer expectedDivision,
        String expectedDivisionName
    ) {
        assertEquals(expectedDivision, division.getDivision());
        assertEquals(expectedDivisionName, division.getDivisionName());
    }

    private void assertExportedDivisionValues(
        ExportableDivisionCi exportableDivisionCi,
        Integer expectedDivision,
        String expectedDivisionName
    ) {
        assertEquals(expectedDivision, exportableDivisionCi.getDivision());
        assertEquals(expectedDivisionName, exportableDivisionCi.getDivisionName());
    }

    private CompetitorCiImpl createFromSapiCompetitorEndpoint(
        SapiCompetitorProfileEndpoint sapiCompetitorProfileEndpoint
    ) {
        return new CompetitorCiImpl(
            anyUrn,
            mock(DataRouterManager.class),
            anyLocale,
            exceptionHandlingStrategy,
            sapiCompetitorProfileEndpoint,
            anyLocale
        );
    }

    private CompetitorCiImpl createWithoutData() {
        return new CompetitorCiImpl(anyUrn, dataRouterManager, anyLocale, exceptionHandlingStrategy);
    }

    private SapiCompetitorProfileEndpoint createWithDivisionAndDivisionName(
        Integer division,
        String divisionName
    ) {
        SapiTeamExtended sapiTeamExtended = new SapiTeamExtended();
        sapiTeamExtended.setDivision(division);
        sapiTeamExtended.setDivisionName(divisionName);
        SapiCompetitorProfileEndpoint sapiCompetitorProfileEndpoint = new SapiCompetitorProfileEndpoint();
        sapiCompetitorProfileEndpoint.setCompetitor(sapiTeamExtended);
        return sapiCompetitorProfileEndpoint;
    }

    private void mockFetchedSummaryEndpointWithoutDivision(CompetitorCiImpl competitorCi) {
        competitorCi.merge(createWithDivisionAndDivisionName(null, null), anyLocale);
    }
}
