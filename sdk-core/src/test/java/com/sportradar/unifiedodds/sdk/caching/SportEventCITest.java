package com.sportradar.unifiedodds.sdk.caching;

import com.google.inject.Injector;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawInfoCI;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.entities.DrawType;
import com.sportradar.unifiedodds.sdk.entities.TimeType;
import com.sportradar.utils.URN;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class SportEventCITest {
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final URN MATCH_EVENT_ID = URN.parse("sr:match:10116681");
    private static final URN RACE_STAGE_EVENT_ID = URN.parse("sr:stage:263714");
    private static final URN TOURNAMENT_EVENT_ID = URN.parse("sr:tournament:1030");
    private static final URN LOTTERY_EVENT_ID = URN.parse("wns:lottery:47125");

    private SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);

    //The cache item is obtained from the cache
    private SportEventCache cache;

    private Injector injector = new TestInjectorFactory(config).create();

    @Before
    public void setup() {
        Mockito.when(config.getDefaultLocale()).thenReturn(LOCALE);

        Mockito.when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);

        cache = injector.getInstance(SportEventCache.class);

        //cache is the callback object (for a http response):
        ((DataRouterImpl) injector.getInstance(DataRouter.class)).setDataListeners(Arrays.asList((DataRouterListener) cache));
    }

    @Ignore("fails due to timezone")
    @Test
    public void getsScheduledDateForMatch() {
        verifyDate(MATCH_EVENT_ID, new Date(116, 7, 10, 2, 0));
    }

    @Ignore("fails due to timezone")
    @Test
    public void getsScheduledDateForRaceStage() {
        verifyDate(RACE_STAGE_EVENT_ID, new Date(116, 8, 23, 9, 0));
    }

    @Ignore("fails due to timezone")
    @Test
    public void getsScheduledDateForTournament() { verifyDate(TOURNAMENT_EVENT_ID, new Date(118, 4, 15, 9, 30)); }

    @Test
    public void getCategoryIdForLottery() throws Exception {
        SportEventCI cacheItem = cache.getEventCacheItem(LOTTERY_EVENT_ID);

        DrawInfoCI drawInfo = ((LotteryCI)cacheItem).getDrawInfo();

        System.out.println();

        assertEquals(DrawType.Drum, drawInfo.getDrawType());
        assertEquals(TimeType.Fixed, drawInfo.getTimeType());
        assertEquals("6/41", drawInfo.getGameType());
    }

    private void verifyDate(URN eventId, Date expected) {
        try {
            SportEventCI cacheItem = cache.getEventCacheItem(eventId);

            Date actual = cacheItem.getScheduled();

            assertEquals(expected, actual);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
