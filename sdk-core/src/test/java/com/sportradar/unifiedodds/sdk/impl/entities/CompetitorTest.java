package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterListener;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.di.TestingModule;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.utils.URN;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CompetitorTest {
    private static final String COMPETITOR_ID = "sr:competitor:3700";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);

    SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);

    SportsInfoManager sportsInfoMgr;

    Injector injector = Guice.createInjector(Modules
            .override(new MockedMasterModule(config))
            .with(new TestingModule(Optional.empty()))
    );

    @Test
    public void parsesEntityFromXml() {
        //Prepare
        Mockito.when(config.getDefaultLocale())
                .thenReturn(LOCALE);

        Mockito.when(config.getDesiredLocales())
                .thenReturn(LOCALES);

        Mockito.when(config.getExceptionHandlingStrategy())
                .thenReturn(ExceptionHandlingStrategy.Throw);

        ((DataRouterImpl) injector.getInstance(DataRouter.class))
                .setDataListeners(Lists.newArrayList(
                        (DataRouterListener) injector.getInstance(ProfileCache.class)
                ));

        sportsInfoMgr = injector.getInstance(SportsInfoManager.class);

        //Execute
        Competitor competitor = sportsInfoMgr.getCompetitor(URN.parse(COMPETITOR_ID));

        //Verify
        String expectedState = "ON"; //Ontario Canada

        Assert.assertEquals(expectedState, competitor.getState());
    }
}
