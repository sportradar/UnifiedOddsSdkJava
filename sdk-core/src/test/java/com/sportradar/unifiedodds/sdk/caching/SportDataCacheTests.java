/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching;

import static org.junit.Assert.*;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Injector;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixture;
import com.sportradar.uf.sportsapi.datamodel.SAPISportCategoriesEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.SportData;
import com.sportradar.unifiedodds.sdk.caching.impl.SportsDataCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.conn.SdkConnListener;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.shared.RestMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.TestDataRouterManager;
import com.sportradar.unifiedodds.sdk.shared.TestFeed;
import com.sportradar.utils.URN;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class SportDataCacheTests {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);
    private static final URN EVENT_ID = URN.parse("sr:match:10116681");
    private static final URN TOURNAMENT_ID = URN.parse("sr:tournament:40");
    private static final URN SPORT_ID1 = URN.parse("sr:sport:1");

    private Cache<URN, SportCI> sportsCache;
    private Cache<URN, CategoryCI> categoriesCache;
    private CacheItemFactory cacheItemFactory;
    private DataRouterManager dataRouterManager;

    private SDKInternalConfiguration configInternal = Mockito.mock(SDKInternalConfiguration.class);

    private Injector injector = new TestInjectorFactory(configInternal).create();

    private SportsDataCache cache;

    private DataRouterImpl dataRouter;

    @Before
    public void setup() {
        Mockito.when(configInternal.getDefaultLocale()).thenReturn(LOCALE);
        Mockito
            .when(configInternal.getExceptionHandlingStrategy())
            .thenReturn(ExceptionHandlingStrategy.Throw);

        final OddsFeedConfiguration config = OddsFeed
            .getOddsFeedConfigurationBuilder()
            .setAccessToken("testuser")
            .selectCustom()
            .setMessagingUsername(Constants.SDK_USERNAME)
            .setMessagingPassword(Constants.SDK_PASSWORD)
            .setMessagingHost(Constants.RABBIT_IP)
            .useMessagingSsl(false)
            .setApiHost(Constants.RABBIT_IP)
            .setDefaultLocale(LOCALE)
            .setMessagingVirtualHost(Constants.UF_VIRTUALHOST)
            .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw)
            .build();

        sportsCache = CacheBuilder.newBuilder().build();
        categoriesCache = CacheBuilder.newBuilder().build();
        cacheItemFactory = injector.getInstance(CacheItemFactory.class);

        SdkConnListener sdkListener = new SdkConnListener();
        TestFeed feed = new TestFeed(sdkListener, config, sdkListener);
        dataRouter = (DataRouterImpl) injector.getInstance(DataRouter.class);
        dataRouterManager = new TestDataRouterManager(feed.TestHttpHelper, dataRouter);

        cache =
            new SportsDataCacheImpl(
                sportsCache,
                categoriesCache,
                cacheItemFactory,
                configInternal,
                dataRouterManager
            );

        dataRouter.setDataListeners(Arrays.asList((DataRouterListener) cache));
    }

    @Test
    public void initialSetupWorks() {
        assertNotNull(sportsCache);
        assertNotNull(categoriesCache);

        assertEquals(0, sportsCache.size());
        assertEquals(0, categoriesCache.size());
    }

    @Test(expected = com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException.class)
    public void retrieveNonExistingSport() throws IllegalCacheStateException, CacheItemNotFoundException {
        assertNotNull(sportsCache);
        assertNotNull(categoriesCache);

        SportData sportData = cache.getSport(SPORT_ID1, LOCALES);
        assertNull(sportData);
    }

    @Test(expected = com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException.class)
    public void retrieveNonExistingCategory() throws IllegalCacheStateException, CacheItemNotFoundException {
        assertNotNull(sportsCache);
        assertNotNull(categoriesCache);

        URN categoryId = URN.parse("sr:category:1");
        CategoryCI categoryCi = cache.getCategory(categoryId, LOCALES);
        assertNull(categoryCi);
    }

    @Test
    public void addNewSportAndCategory() throws IllegalCacheStateException, CacheItemNotFoundException {
        assertEquals(0, sportsCache.size());
        assertEquals(0, categoriesCache.size());

        final int categoryCount = 10;
        final int categoryFactor = 15;

        SAPISportCategoriesEndpoint sportCategoriesEndpoint = RestMessageBuilder.getSportCategories(
            (int) SPORT_ID1.getId(),
            categoryCount,
            categoryFactor
        );
        dataRouter.onSportCategoriesFetched(sportCategoriesEndpoint, LOCALE, null);

        assertEquals(1, sportsCache.size());
        assertEquals(categoryCount, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, LOCALES);
        assertNotNull(sport);
        assertEquals(SPORT_ID1, sport.getId());
        assertEquals(categoryCount, sport.getCategories().size());
    }

    @Test
    public void addNewCategoryToExistingSport()
        throws IllegalCacheStateException, CacheItemNotFoundException {
        addNewSportAndCategory();

        final int categoryCount = 10;
        final int categoryFactor = 1100;

        assertEquals(1, sportsCache.size());
        assertEquals(categoryCount, categoriesCache.size());

        SAPISportCategoriesEndpoint sportCategoriesEndpoint = RestMessageBuilder.getSportCategories(
            (int) SPORT_ID1.getId(),
            categoryCount,
            categoryFactor
        );
        dataRouter.onSportCategoriesFetched(sportCategoriesEndpoint, LOCALE, null);

        assertEquals(1, sportsCache.size());
        assertEquals(categoryCount * 2, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, LOCALES);
        assertNotNull(sport);
        assertEquals(SPORT_ID1, sport.getId());
        assertEquals(categoryCount * 2, sport.getCategories().size());
    }

    @Test
    public void addNewSportAndCategoryFromFixture()
        throws IllegalCacheStateException, CacheItemNotFoundException {
        assertEquals(0, sportsCache.size());
        assertEquals(0, categoriesCache.size());

        final int eventId = 123456;
        final int categoryId = 1234;

        SAPIFixture fixture = RestMessageBuilder.getFixture(eventId, (int) SPORT_ID1.getId(), categoryId);
        dataRouter.onFixtureFetched(URN.parse(fixture.getId()), fixture, LOCALE, null);

        assertEquals(1, sportsCache.size());
        assertEquals(1, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, LOCALES);
        assertNotNull(sport);
        assertEquals(SPORT_ID1, sport.getId());
        assertEquals(1, sport.getCategories().size());
    }

    @Test
    public void addNewCategoryToExistingSportFromFixture()
        throws IllegalCacheStateException, CacheItemNotFoundException {
        addNewSportAndCategoryFromFixture();
        assertEquals(1, sportsCache.size());
        assertEquals(1, categoriesCache.size());

        final int eventId = 123456;
        final int categoryId = 4321;

        SAPIFixture fixture = RestMessageBuilder.getFixture(eventId, (int) SPORT_ID1.getId(), categoryId);
        dataRouter.onFixtureFetched(URN.parse(fixture.getId()), fixture, LOCALE, null);

        assertEquals(1, sportsCache.size());
        assertEquals(2, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, LOCALES);
        assertNotNull(sport);
        assertEquals(SPORT_ID1, sport.getId());
        assertEquals(2, sport.getCategories().size());
    }
}
