/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Injector;
import com.sportradar.uf.sportsapi.datamodel.SapiFixture;
import com.sportradar.uf.sportsapi.datamodel.SapiSportCategoriesEndpoint;
import com.sportradar.unifiedodds.sdk.conn.UofConnListener;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportData;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportsDataCacheImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.shared.TestDataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.shared.TestFeed;
import com.sportradar.unifiedodds.sdk.shared.RestMessageBuilder;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Random;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({ "checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity" })
public class SportDataCacheTests {

    private static final Urn SPORT_ID1 = Urn.parse("sr:sport:1");
    private Cache<Urn, SportCi> sportsCache;
    private Cache<Urn, CategoryCi> categoriesCache;

    private SportsDataCache cache;
    private DataRouterImpl dataRouter;

    @Before
    public void setup() {
        final StubUofConfiguration config = new StubUofConfiguration();

        Injector injector = new TestInjectorFactory(mock(SdkInternalConfiguration.class), config).create();

        sportsCache = CacheBuilder.newBuilder().build();
        categoriesCache = CacheBuilder.newBuilder().build();
        CacheItemFactory cacheItemFactory = injector.getInstance(CacheItemFactory.class);

        UofConnListener sdkListener = new UofConnListener();
        TestFeed feed = new TestFeed(sdkListener, config, sdkListener);
        dataRouter = (DataRouterImpl) injector.getInstance(DataRouter.class);
        DataRouterManager dataRouterManager = new TestDataRouterManager(feed.TestHttpHelper, dataRouter);

        cache = new SportsDataCacheImpl(sportsCache, categoriesCache, cacheItemFactory, dataRouterManager);

        dataRouter.setDataListeners(asList((DataRouterListener) cache));
    }

    @Test
    public void initialSetupWorks() {
        assertNotNull(sportsCache);
        assertNotNull(categoriesCache);

        assertEquals(0, sportsCache.size());
        assertEquals(0, categoriesCache.size());
    }

    @Test(expected = com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException.class)
    public void retrieveNonExistingSport() throws IllegalCacheStateException, CacheItemNotFoundException {
        assertNotNull(sportsCache);
        assertNotNull(categoriesCache);

        SportData sportData = cache.getSport(SPORT_ID1, asList());
        assertNull(sportData);
    }

    @Test(expected = com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException.class)
    public void retrieveNonExistingCategory() throws IllegalCacheStateException, CacheItemNotFoundException {
        assertNotNull(sportsCache);
        assertNotNull(categoriesCache);

        Urn categoryId = Urn.parse("sr:category:1");
        CategoryCi categoryCi = cache.getCategory(categoryId, asList());
        assertNull(categoryCi);
    }

    @Test
    public void addNewSportAndCategory() throws IllegalCacheStateException, CacheItemNotFoundException {
        assertEquals(0, sportsCache.size());
        assertEquals(0, categoriesCache.size());

        final int categoryCount = 10;
        final int categoryFactor = 15;

        SapiSportCategoriesEndpoint sportCategoriesEndpoint = RestMessageBuilder.getSportCategories(
            (int) SPORT_ID1.getId(),
            categoryCount,
            categoryFactor
        );
        dataRouter.onSportCategoriesFetched(sportCategoriesEndpoint, anyLanguage(), null);

        assertEquals(1, sportsCache.size());
        assertEquals(categoryCount, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, asList());
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

        SapiSportCategoriesEndpoint sportCategoriesEndpoint = RestMessageBuilder.getSportCategories(
            (int) SPORT_ID1.getId(),
            categoryCount,
            categoryFactor
        );
        dataRouter.onSportCategoriesFetched(sportCategoriesEndpoint, anyLanguage(), null);

        assertEquals(1, sportsCache.size());
        assertEquals(categoryCount * 2, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, asList());
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

        SapiFixture fixture = RestMessageBuilder.getFixture(eventId, (int) SPORT_ID1.getId(), categoryId);
        dataRouter.onFixtureFetched(Urn.parse(fixture.getId()), fixture, anyLanguage(), null);

        assertEquals(1, sportsCache.size());
        assertEquals(1, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, asList());
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

        SapiFixture fixture = RestMessageBuilder.getFixture(eventId, (int) SPORT_ID1.getId(), categoryId);
        dataRouter.onFixtureFetched(Urn.parse(fixture.getId()), fixture, anyLanguage(), null);

        assertEquals(1, sportsCache.size());
        assertEquals(2, categoriesCache.size());

        SportData sport = cache.getSport(SPORT_ID1, asList());
        assertNotNull(sport);
        assertEquals(SPORT_ID1, sport.getId());
        assertEquals(2, sport.getCategories().size());
    }

    private Locale anyLanguage() {
        val localePool = asList(Locale.CHINESE, Locale.FRENCH, Locale.CANADA, Locale.KOREA, Locale.GERMANY);
        return localePool.get(new Random().nextInt(localePool.size()));
    }
}
