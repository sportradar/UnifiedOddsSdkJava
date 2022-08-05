package com.sportradar.unifiedodds.sdk.caching;

import com.google.inject.Injector;
import com.sportradar.uf.sportsapi.datamodel.SAPITeam;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentGroup;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ci.GroupCI;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.impl.TestingDataProvider;
import com.sportradar.utils.URN;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class TournamentCITest {
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);
    private static final URN TOURNAMENT_EVENT_ID_1030 = URN.parse("sr:tournament:1030");
    private static final URN TOURNAMENT_EVENT_ID_40 = URN.parse("sr:tournament:40");

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
    public void getsScheduledDateForTournament() { verifyDate(TOURNAMENT_EVENT_ID_1030, new Date(118, 4, 15, 9, 30)); }

    @Test
    public void mergeTournamentGroupBase() throws Exception {
        SAPITournamentInfoEndpoint sapiTournament = new TestingDataProvider<SAPITournamentInfoEndpoint>("test/rest/tournament_info.xml").getData();
        SportEventCI cacheItem = cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        TournamentCI tour40 = ((TournamentCI) cacheItem);
        assertNotNull(tour40);
        assertNotNull(sapiTournament);
        tour40.merge(sapiTournament, LOCALE);
        assertEquals(URN.parse(sapiTournament.getTournament().getId()), tour40.getId());
        assertNotNull(sapiTournament.getGroups());
        List<GroupCI> groups = tour40.getGroups(LOCALES);
        assertNotNull(groups);

        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));
    }

    @Test
    public void mergeTournamentGroupRemoveById() throws Exception {
        SAPITournamentInfoEndpoint sapiTournament = new TestingDataProvider<SAPITournamentInfoEndpoint>("test/rest/tournament_info.xml").getData();
        TournamentCI tour40 = (TournamentCI)cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        // set group id
        sapiTournament.getGroups().getGroup().get(0).setId("1");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // change group id
        sapiTournament.getGroups().getGroup().get(0).setId("2");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // remove group
        sapiTournament.getGroups().getGroup().remove(0);
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));
    }

    @Test
    public void mergeTournamentGroupRemoveByName() throws Exception {
        SAPITournamentInfoEndpoint sapiTournament = new TestingDataProvider<SAPITournamentInfoEndpoint>("test/rest/tournament_info.xml").getData();
        TournamentCI tour40 = (TournamentCI)cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        // set group name
        sapiTournament.getGroups().getGroup().get(0).setName("Name1");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // change group name
        sapiTournament.getGroups().getGroup().get(0).setName("Name2");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // remove group
        sapiTournament.getGroups().getGroup().remove(0);
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));
    }

    @Test
    public void mergeTournamentGroupSplit() throws Exception {
        SAPITournamentInfoEndpoint sapiTournament = new TestingDataProvider<SAPITournamentInfoEndpoint>("test/rest/tournament_info.xml").getData();
        TournamentCI tour40 = (TournamentCI)cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        // default
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // set group name
        sapiTournament.getGroups().getGroup().get(0).setId("1");
        sapiTournament.getGroups().getGroup().get(0).setName("Name1");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // split group
        SAPITournamentGroup newGroup = new SAPITournamentGroup();
        newGroup.setId("2");
        newGroup.setName("Name2");
        newGroup.getCompetitor();
        int i = sapiTournament.getGroups().getGroup().get(0).getCompetitor().size() / 2;
        while(i > 0){
            newGroup.getCompetitor().add(sapiTournament.getGroups().getGroup().get(0).getCompetitor().get(i));
            sapiTournament.getGroups().getGroup().get(0).getCompetitor().remove(i);
            i--;
        }
        sapiTournament.getGroups().getGroup().add(newGroup);
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // remove group
        sapiTournament.getGroups().getGroup().remove(0);
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));
    }

    @Test
    public void mergeTournamentGroupChangeCompetitor() throws Exception {
        SAPITournamentInfoEndpoint sapiTournament = new TestingDataProvider<SAPITournamentInfoEndpoint>("test/rest/tournament_info.xml").getData();
        TournamentCI tour40 = (TournamentCI)cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // change competitor id
        sapiTournament.getGroups().getGroup().get(0).getCompetitor().get(0).setId("sr:competitor:17710");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // remove competitor
        sapiTournament.getGroups().getGroup().get(0).getCompetitor().remove(0);
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));
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

    private void verifyTournamentGroups(List<SAPITournamentGroup> sapiGroups, List<GroupCI> ciGroups){
        if(sapiGroups == null || sapiGroups.isEmpty()){
            assertTrue(ciGroups.isEmpty());
            return;
        }

        assertEquals(sapiGroups.size(), ciGroups.size());
        for(SAPITournamentGroup sapiGroup : sapiGroups){
            if(!isNullOrEmpty(sapiGroup.getId())){
                assertTrue(ciGroups.stream().anyMatch(m -> m.getId().equals(sapiGroup.getId())));
            }
            if(!isNullOrEmpty(sapiGroup.getName())){
                assertTrue(ciGroups.stream().anyMatch(m -> m.getName().equals(sapiGroup.getName())));
            }
            GroupCI matchingGroup = ciGroups.stream()
                    .filter(f -> (!isNullOrEmpty(sapiGroup.getId()) && f.getId().equals(sapiGroup.getId()))
                            || (!isNullOrEmpty(sapiGroup.getName()) && f.getName().equals(sapiGroup.getName()))
                            || (isNullOrEmpty(sapiGroup.getId()) && isNullOrEmpty(sapiGroup.getName()) && isNullOrEmpty(f.getId()) && isNullOrEmpty(f.getName()))).findFirst().get();

            assertEquals(sapiGroup.getCompetitor().size(), matchingGroup.getCompetitorIds().size());

            for(SAPITeam sapiCompetitor : sapiGroup.getCompetitor()){
                assertTrue(matchingGroup.getCompetitorIds().contains(URN.parse(sapiCompetitor.getId())));
            }
        }
    }

    private boolean isNullOrEmpty(String input)
    {
        return input == null || input.isEmpty();
    }
}
