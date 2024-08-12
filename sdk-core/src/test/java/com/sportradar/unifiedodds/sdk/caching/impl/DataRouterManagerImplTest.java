/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiFixtureChangesEndpoint;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.utils.Urn;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DataRouterManagerImplTest {

    private DataProvider fixtureChangeDataProvider = mock(DataProvider.class);
    private final DataRouterManagerImpl dataRouterManager = new DataRouterManagerImpl(
        mock(SdkInternalConfiguration.class),
        mock(SdkTaskScheduler.class),
        mock(SdkProducerManager.class),
        mock(DataRouter.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        fixtureChangeDataProvider,
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class)
    );

    @Test
    public void fixtureChangeShouldBeRequestedWithoutAfterDateAndSportId() throws Exception {
        Locale anyLocale = Locale.ENGLISH;
        SapiFixtureChangesEndpoint fixtureChangesEndpoint = mock(SapiFixtureChangesEndpoint.class);

        when(fixtureChangeDataProvider.getData(any(), any())).thenReturn(fixtureChangesEndpoint);
        when(fixtureChangesEndpoint.getFixtureChange()).thenReturn(new ArrayList<>());

        dataRouterManager.requestFixtureChanges(null, null, anyLocale);
        String noQueryParameters = "";
        verify(fixtureChangeDataProvider, times(1)).getData(anyLocale, noQueryParameters);
    }

    @Test
    public void fixtureChangeShouldBeRequestedWithSportId() throws Exception {
        Locale anyLocale = Locale.ENGLISH;
        SapiFixtureChangesEndpoint fixtureChangesEndpoint = mock(SapiFixtureChangesEndpoint.class);

        when(fixtureChangeDataProvider.getData(any(), any())).thenReturn(fixtureChangesEndpoint);
        when(fixtureChangesEndpoint.getFixtureChange()).thenReturn(new ArrayList<>());

        Urn anySportId = new Urn("sr", "sport", 1);
        dataRouterManager.requestFixtureChanges(null, anySportId, anyLocale);

        String queryWithSportIdUrn = "?sportId=sr%3Asport%3A1";
        verify(fixtureChangeDataProvider, times(1)).getData(anyLocale, queryWithSportIdUrn);
    }

    @Test
    public void fixtureChangeShouldBeRequestedWithAfterDate() throws Exception {
        Locale anyLocale = Locale.ENGLISH;
        SapiFixtureChangesEndpoint fixtureChangesEndpoint = mock(SapiFixtureChangesEndpoint.class);

        when(fixtureChangeDataProvider.getData(any(), any())).thenReturn(fixtureChangesEndpoint);
        when(fixtureChangesEndpoint.getFixtureChange()).thenReturn(new ArrayList<>());

        Date anyDate = new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-26");
        dataRouterManager.requestFixtureChanges(anyDate, null, anyLocale);

        String expectedQueryParamaterName = "?afterDateTime";
        verify(fixtureChangeDataProvider, times(1))
            .getData(any(Locale.class), Mockito.contains(expectedQueryParamaterName));
    }
}
