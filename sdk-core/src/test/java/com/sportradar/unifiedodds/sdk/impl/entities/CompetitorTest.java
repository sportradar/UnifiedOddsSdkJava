package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterListener;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.di.TestingModule;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import com.sportradar.utils.Urn;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.val;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "VisibilityModifier", "MagicNumber" })
public class CompetitorTest {

    private static final String COMPETITOR_ID = "sr:competitor:3700";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);

    StubUofConfiguration config = new StubUofConfiguration();

    SportDataProvider sportDataProvider;

    @Test
    public void parsesEntityFromXml() {
        val internalConfig = configurationWithAnyDefaulAndDesiredLanguagesAndThrowingOnErrors();
        Injector injector = Guice.createInjector(
            Modules
                .override(new MockedMasterModule(internalConfig, config))
                .with(new TestingModule(Optional.empty()))
        );

        ((DataRouterImpl) injector.getInstance(DataRouter.class)).setDataListeners(
                Lists.newArrayList((DataRouterListener) injector.getInstance(ProfileCache.class))
            );

        sportDataProvider = injector.getInstance(SportDataProvider.class);

        //Execute
        Competitor competitor = sportDataProvider.getCompetitor(Urn.parse(COMPETITOR_ID));

        //Verify
        String expectedState = "ON"; //Ontario Canada

        Assert.assertEquals(expectedState, competitor.getState());
    }

    private static SdkInternalConfiguration configurationWithAnyDefaulAndDesiredLanguagesAndThrowingOnErrors() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        when(mock.getDesiredLocales()).thenReturn(asList(Locale.UK));
        when(mock.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        when(mock.getHttpClientTimeout()).thenReturn(10);
        when(mock.getFastHttpClientTimeout()).thenReturn(5L);
        return mock;
    }
}
