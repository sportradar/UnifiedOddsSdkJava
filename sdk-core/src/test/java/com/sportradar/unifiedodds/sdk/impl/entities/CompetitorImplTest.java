/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.ci.DivisionCi;
import com.sportradar.unifiedodds.sdk.entities.Division;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.Urn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class CompetitorImplTest {

    private final Urn competitorUrn = new Urn("sr", "competitor", 1);
    private final boolean isVirtual = false;
    private final ProfileCache profileCache = mock(ProfileCache.class);
    private final List<Locale> locales = new ArrayList<>();
    private final SportEntityFactory sportEntityFactory = mock(SportEntityFactory.class);
    private final ExceptionHandlingStrategy exceptionHandlingStrategy = ExceptionHandlingStrategy.Throw;
    private final CompetitorCi competitorCi = mock(CompetitorCi.class);
    private final CompetitorImpl competitor = create();
    private final Integer anyDivisionId = 1;
    private final String anyDivisionName = "FBS";

    @Test
    public void shouldReturnDivisionWhenCompetitorCiIsInCache()
        throws IllegalCacheStateException, CacheItemNotFoundException {
        when(profileCache.getCompetitorProfile(any(), any())).thenReturn(competitorCi);
        when(competitorCi.getDivision()).thenReturn(new DivisionCi(anyDivisionId, anyDivisionName));
        Division actualDivision = competitor.getDivision();
        assertDivisionValues(actualDivision, anyDivisionId, anyDivisionName);
    }

    @Test
    public void shouldReturnNullDivisionWhenCompetitorNotInCache()
        throws IllegalCacheStateException, CacheItemNotFoundException {
        when(profileCache.getCompetitorProfile(any(), any())).thenReturn(null);
        Division actualDivision = competitor.getDivision();
        assertNull(actualDivision);
    }

    @Test
    public void shouldReturnNullDivisionWhenDivisionMissingFromCompetitorCi()
        throws IllegalCacheStateException, CacheItemNotFoundException {
        when(profileCache.getCompetitorProfile(any(), any())).thenReturn(competitorCi);
        when(competitorCi.getDivision()).thenReturn(null);
        Division actualDivision = competitor.getDivision();
        assertNull(actualDivision);
    }

    private void assertDivisionValues(
        Division division,
        Integer expectedDivisionId,
        String expectedDivisionName
    ) {
        assertEquals(expectedDivisionId, division.getDivision());
        assertEquals(expectedDivisionName, division.getDivisionName());
    }

    private CompetitorImpl create() {
        return new CompetitorImpl(
            competitorUrn,
            profileCache,
            new HashMap<>(),
            isVirtual,
            locales,
            sportEntityFactory,
            exceptionHandlingStrategy
        );
    }
}
