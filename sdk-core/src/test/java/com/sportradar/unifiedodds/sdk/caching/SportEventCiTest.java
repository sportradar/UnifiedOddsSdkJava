package com.sportradar.unifiedodds.sdk.caching;

import static com.sportradar.unifiedodds.sdk.caching.DateConverterToCentralEurope.convertFrom;
import static com.sportradar.utils.Urns.Lotteries.getForAnyLottery;
import static com.sportradar.utils.Urns.SportEvents.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawInfoCi;
import com.sportradar.unifiedodds.sdk.caching.impl.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactoryImplConstructor;
import com.sportradar.unifiedodds.sdk.entities.DrawType;
import com.sportradar.unifiedodds.sdk.entities.TimeType;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.MappingTypeProviderImplConstructor;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.val;
import org.junit.Test;

@SuppressWarnings({ "ClassFanOutComplexity", "IllegalCatch", "MagicNumber", "ClassDataAbstractionCoupling" })
public class SportEventCiTest {

    private static final Urn LOTTERY_EVENT_ID = Urn.parse("wns:lottery:47125");
    private DataRouterImpl dataRouter = new DataRouterImpl();

    @Test
    public void getsScheduledDateForMatch()
        throws CacheItemNotFoundException, DataProviderException, DatatypeConfigurationException {
        SapiSportEvent match = new SapiSportEvent();
        match.setId(getForAnyMatch().toString());
        match.setScheduled(april10th2016());
        match.setTournament(tournamentWithCategory());
        SapiMatchSummaryEndpoint matchSummary = new SapiMatchSummaryEndpoint();
        matchSummary.setSportEvent(match);

        val summaries = mock(DataProvider.class);

        when(summaries.getData(any(Locale.class), anyString())).thenReturn(matchSummary);

        SdkInternalConfiguration config = configurationWithAnyLanguageThrowingOnErrors();
        CacheItemFactory ciFactory = CacheItemFactoryImplConstructor.create(
            DataRouterManagerBuilder.create().with(dataRouter).withSummaries(summaries).build(),
            config,
            mock(Cache.class)
        );
        SportEventCacheImpl cache = SportEventCacheImplConstructor.create(
            ciFactory,
            MappingTypeProviderImplConstructor.create(),
            mock(DataRouterManager.class),
            mock(SdkInternalConfiguration.class),
            CacheBuilder.newBuilder().build()
        );
        dataRouter.setDataListeners(asList(cache));

        SportEventCi cacheItem = cache.getEventCacheItem(getForAnyMatch());
        Date actual = cacheItem.getScheduled();

        assertEquals(new Date(116, 7, 10, 2, 0), convertFrom(actual, ZoneId.systemDefault()));
    }

    @Test
    public void getsScheduledDateForRaceStage()
        throws CacheItemNotFoundException, DataProviderException, DatatypeConfigurationException {
        SapiSportEvent stage = new SapiSportEvent();
        stage.setId(urnForAnyStage().toString());
        stage.setTournament(tournamentWithCategory());
        stage.setScheduled(april10th2016());
        SapiStageSummaryEndpoint stageSummary = new SapiStageSummaryEndpoint();
        stageSummary.setSportEvent(stage);

        val summaries = mock(DataProvider.class);

        when(summaries.getData(any(Locale.class), anyString())).thenReturn(stageSummary);

        SdkInternalConfiguration langConfig = configurationWithAnyLanguage();
        SdkInternalConfiguration langConfigThrowing = configurationWithAnyLanguageThrowingOnErrors();
        CacheItemFactory ciFactory = CacheItemFactoryImplConstructor.create(
            mock(DataRouterManager.class),
            langConfigThrowing,
            mock(Cache.class)
        );
        SportEventCacheImpl cache = SportEventCacheImplConstructor.create(
            ciFactory,
            MappingTypeProviderImplConstructor.create(),
            DataRouterManagerBuilder.create().with(dataRouter).withSummaries(summaries).build(),
            langConfig,
            CacheBuilder.newBuilder().build()
        );
        dataRouter.setDataListeners(asList(cache));

        SportEventCi cacheItem = cache.getEventCacheItem(urnForAnyStage());
        Date actual = cacheItem.getScheduled();

        assertEquals(new Date(116, 7, 10, 2, 0), convertFrom(actual, ZoneId.systemDefault()));
    }

    @Test
    public void getsScheduledDateForTournament()
        throws CacheItemNotFoundException, DataProviderException, DatatypeConfigurationException {
        SapiTournamentInfoEndpoint tournamentSummary = new SapiTournamentInfoEndpoint();
        SapiTournamentExtended tournament = new SapiTournamentExtended();
        tournament.setId(urnForAnyTournament().toString());
        tournament.setScheduled(april10th2016());
        tournamentSummary.setTournament(tournament);

        val summaries = mock(DataProvider.class);
        when(summaries.getData(any(Locale.class), anyString())).thenReturn(tournamentSummary);

        SdkInternalConfiguration config = configurationWithAnyLanguageThrowingOnErrors();
        CacheItemFactory ciFactory = CacheItemFactoryImplConstructor.create(
            DataRouterManagerBuilder.create().with(dataRouter).withSummaries(summaries).build(),
            config,
            mock(Cache.class)
        );
        SportEventCacheImpl cache = SportEventCacheImplConstructor.create(
            ciFactory,
            MappingTypeProviderImplConstructor.create(),
            mock(DataRouterManager.class),
            mock(SdkInternalConfiguration.class),
            CacheBuilder.newBuilder().build()
        );
        dataRouter.setDataListeners(asList(cache));

        SportEventCi cacheItem = cache.getEventCacheItem(urnForAnyTournament());
        Date actual = cacheItem.getScheduled();

        assertEquals(new Date(116, 7, 10, 2, 0), convertFrom(actual, ZoneId.systemDefault()));
    }

    @Test
    public void getCategoryIdForLottery() throws Exception {
        val lotterySchedules = mock(DataProvider.class);
        SapiLotterySchedule schedule = createLotteryWithIdAndDraw(
            getForAnyLottery(),
            fixedTimeDrumDrawWithGameType("6/41")
        );
        when(lotterySchedules.getData(any(Locale.class), anyString())).thenReturn(schedule);

        SdkInternalConfiguration config = configurationWithAnyLanguageThrowingOnErrors();
        CacheItemFactory ciFactory = CacheItemFactoryImplConstructor.create(
            DataRouterManagerBuilder.create().with(dataRouter).setLotterySchedules(lotterySchedules).build(),
            config,
            mock(Cache.class)
        );
        SportEventCacheImpl cache = SportEventCacheImplConstructor.create(
            ciFactory,
            MappingTypeProviderImplConstructor.create(),
            mock(DataRouterManager.class),
            mock(SdkInternalConfiguration.class),
            CacheBuilder.newBuilder().build()
        );
        dataRouter.setDataListeners(asList(cache));

        SportEventCi cacheItem = cache.getEventCacheItem(LOTTERY_EVENT_ID);
        DrawInfoCi drawInfo = ((LotteryCi) cacheItem).getDrawInfo();

        assertEquals(DrawType.Drum, drawInfo.getDrawType());
        assertEquals(TimeType.Fixed, drawInfo.getTimeType());
        assertEquals("6/41", drawInfo.getGameType());
    }

    private static SdkInternalConfiguration configurationWithAnyLanguageThrowingOnErrors() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        when(mock.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        return mock;
    }

    private static SdkInternalConfiguration configurationWithAnyLanguage() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        return mock;
    }

    private SapiLottery.SapiDrawInfo fixedTimeDrumDrawWithGameType(String type) {
        SapiLottery.SapiDrawInfo draw = new SapiLottery.SapiDrawInfo();
        draw.setGameType(type);
        draw.setTimeType(SapiTimeType.FIXED);
        draw.setDrawType(SapiDrawType.DRUM);
        return draw;
    }

    private SapiLotterySchedule createLotteryWithIdAndDraw(Urn id, SapiLottery.SapiDrawInfo draw) {
        SapiLotterySchedule schedule = new SapiLotterySchedule();
        SapiLottery lottery = new SapiLottery();
        lottery.setId(id.toString());

        lottery.setDrawInfo(draw);
        schedule.setLottery(lottery);
        return schedule;
    }

    private void verifyDate(Urn eventId, Object matchSummary, Date expected)
        throws CacheItemNotFoundException, DataProviderException, DatatypeConfigurationException {}

    private static XMLGregorianCalendar april10th2016() throws DatatypeConfigurationException {
        val calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        calendar.setYear(2016);
        calendar.setMonth(8);
        calendar.setDay(10);
        return calendar;
    }

    private static SapiTournament tournamentWithCategory() {
        SapiTournament tournament = new SapiTournament();
        tournament.setId(urnForAnyTournament().toString());
        SapiCategory category = new SapiCategory();
        category.setId(Urns.Categories.urnForAnyCategory().toString());
        tournament.setCategory(category);
        return tournament;
    }
}
