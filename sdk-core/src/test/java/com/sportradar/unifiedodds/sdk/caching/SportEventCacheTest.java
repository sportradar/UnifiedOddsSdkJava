package com.sportradar.unifiedodds.sdk.caching;

import com.google.inject.Injector;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSdkCache;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.URN;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.*;

public class SportEventCacheTest {
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final URN EVENT_ID = URN.parse("sr:match:10116681");
    private static final URN TOURNAMENT_ID = URN.parse("sr:tournament:40");

    SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);

    private Injector injector = new TestInjectorFactory(config).create();

    private SportEventCache cache;

    @Before
    public void setup() {
        Mockito.when(config.getDefaultLocale())
                .thenReturn(LOCALE);

        Mockito.when(config.getExceptionHandlingStrategy())
                .thenReturn(ExceptionHandlingStrategy.Throw);

        cache = injector.getInstance(SportEventCache.class);

        //cache is the callback object (for a http response):
        ((DataRouterImpl) injector.getInstance(DataRouter.class))
                .setDataListeners(Arrays.asList((DataRouterListener) cache));
    }

    @Test
    public void getsEventCacheItem() throws CacheItemNotFoundException {
        SportEventCI item = cache.getEventCacheItem(EVENT_ID);

        assertNotNull(item);
        assertEquals(EVENT_ID, item.getId());
    }

    @Test
    public void getsEventIdsForTournament() throws IllegalCacheStateException {
        List<URN> eventIds = cache.getEventIds(TOURNAMENT_ID, LOCALE);

        assertThat(eventIds.size(), Matchers.greaterThan(0));
    }

    @Test
    public void getsEventIdsForDate() throws IllegalCacheStateException {
        List<URN> eventIds = cache.getEventIds(new Date(120, 0, 29), LOCALE);

        assertThat(eventIds.size(), Matchers.greaterThan(0));
    }

    @Test
    public void purgesCacheItem() throws Exception {
        //Prepare
        List<URN> eventIds = cache.getEventIds(new Date(120, 0, 29), LOCALE);

        Map<String, Long> mapBefore = ((ExportableSdkCache) cache).cacheStatus();
        long totalBefore = mapBefore
                .values()
                .stream()
                .reduce((x, acc) -> acc + x)
                .get();

        //Execute
        cache.purgeCacheItem(eventIds.get(0));

        //Verify
        Map<String, Long> mapAfter = ((ExportableSdkCache) cache).cacheStatus();
        long totalAfter = mapAfter
                .values()
                .stream()
                .reduce((x, acc) -> acc + x)
                .get();

        //one item has been purged from the cache
        assertTrue(totalBefore == totalAfter + 1);
    }

    @Test
    public void onEventBookedSetsStatusToBooked() throws Exception {
        //Prepare
        List<URN> eventIds = cache.getEventIds(TOURNAMENT_ID, LOCALE);

        URN eventId = eventIds.get(0);

        //Execute
        cache.onEventBooked(eventId);

        //Verify
        CompetitionCI item = (CompetitionCI) cache.getEventCacheItem(eventId);
        assertEquals(BookingStatus.Booked, item.getBookingStatus());
    }

    @Test
    public void deletesSportEventsBeforeGivenDate() throws Exception {
        //Prepare
        List<URN> eventIds = cache.getEventIds(new Date(120, 0, 29), LOCALE);

        //Execute
        int numOfDeleted = cache.deleteSportEventsFromCache(new Date(120, 0, 31)); //two days later

        //Verify
        assertEquals(eventIds.size(), numOfDeleted);
    }
}
