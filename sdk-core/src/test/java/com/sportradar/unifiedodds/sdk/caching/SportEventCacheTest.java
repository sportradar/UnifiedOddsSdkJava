package com.sportradar.unifiedodds.sdk.caching;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Injector;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSdkCache;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import com.sportradar.utils.Urn;
import java.util.*;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "VisibilityModifier" })
public class SportEventCacheTest {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final Urn EVENT_ID = Urn.parse("sr:match:10116681");
    private static final Urn TOURNAMENT_ID = Urn.parse("sr:tournament:40");

    private Injector injector;

    private SportEventCache cache;

    @BeforeEach
    public void setup() {
        val config = configurationWithAnyLanguageThrowingOnErrors();
        injector = new TestInjectorFactory(config, new StubUofConfiguration()).create();

        cache = injector.getInstance(SportEventCache.class);

        //cache is the callback object (for a http response):
        ((DataRouterImpl) injector.getInstance(DataRouter.class)).setDataListeners(
                Arrays.asList((DataRouterListener) cache)
            );
    }

    @Test
    public void getsEventCacheItem() throws CacheItemNotFoundException {
        SportEventCi item = cache.getEventCacheItem(EVENT_ID);

        assertNotNull(item);
        assertEquals(EVENT_ID, item.getId());
    }

    @Test
    public void getsEventIdsForTournament() throws IllegalCacheStateException {
        List<Urn> eventIds = cache.getEventIds(TOURNAMENT_ID, LOCALE);

        assertThat(eventIds.size(), Matchers.greaterThan(0));
    }

    @Test
    public void getsEventIdsForDate() throws IllegalCacheStateException {
        List<Urn> eventIds = cache.getEventIds(new Date(120, 0, 29), LOCALE);

        assertThat(eventIds.size(), Matchers.greaterThan(0));
    }

    @Test
    public void purgesCacheItem() throws Exception {
        //Prepare
        List<Urn> eventIds = cache.getEventIds(new Date(120, 0, 29), LOCALE);

        Map<String, Long> mapBefore = ((ExportableSdkCache) cache).cacheStatus();
        long totalBefore = mapBefore.values().stream().reduce((x, acc) -> acc + x).get();

        //Execute
        cache.purgeCacheItem(eventIds.get(0));

        //Verify
        Map<String, Long> mapAfter = ((ExportableSdkCache) cache).cacheStatus();
        long totalAfter = mapAfter.values().stream().reduce((x, acc) -> acc + x).get();

        //one item has been purged from the cache
        assertTrue(totalBefore == totalAfter + 1);
    }

    @Test
    public void onEventBookedSetsStatusToBooked() throws Exception {
        //Prepare
        List<Urn> eventIds = cache.getEventIds(TOURNAMENT_ID, LOCALE);

        Urn eventId = eventIds.get(0);

        //Execute
        cache.onEventBooked(eventId);

        //Verify
        CompetitionCi item = (CompetitionCi) cache.getEventCacheItem(eventId);
        assertEquals(BookingStatus.Booked, item.getBookingStatus());
    }

    @Test
    public void deletesSportEventsBeforeGivenDate() throws Exception {
        //Prepare
        List<Urn> eventIds = cache.getEventIds(new Date(120, 0, 29), LOCALE);

        //Execute
        int numOfDeleted = cache.deleteSportEventsFromCache(new Date(120, 0, 31)); //two days later

        //Verify
        assertEquals(eventIds.size(), numOfDeleted);
    }

    private static SdkInternalConfiguration configurationWithAnyLanguageThrowingOnErrors() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        when(mock.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        return mock;
    }
}
