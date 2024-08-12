package com.sportradar.unifiedodds.sdk.caching;

import static com.sportradar.unifiedodds.sdk.caching.DateConverterToCentralEurope.convertFrom;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Injector;
import com.sportradar.uf.sportsapi.datamodel.SapiTeam;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentGroup;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.ci.GroupCi;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.impl.TestingDataProvider;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import com.sportradar.utils.Urn;
import java.util.*;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings(
    {
        "BooleanExpressionComplexity",
        "ClassFanOutComplexity",
        "CyclomaticComplexity",
        "IllegalCatch",
        "MagicNumber",
        "MultipleStringLiterals",
        "UnnecessaryParentheses",
    }
)
public class TournamentCiTest {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);
    private static final Urn TOURNAMENT_EVENT_ID_40 = Urn.parse("sr:tournament:40");

    private SportEventCache cache;

    private Injector injector;

    @BeforeEach
    public void setup() {
        //        Mockito.when(config.getDefaultLocale()).thenReturn(LOCALE);
        //
        //        Mockito.when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);

        val config = configurationWithAnyLanguageThrowingOnErrors();
        injector = new TestInjectorFactory(config, new StubUofConfiguration()).create();

        cache = injector.getInstance(SportEventCache.class);

        //cache is the callback object (for a http response):
        ((DataRouterImpl) injector.getInstance(DataRouter.class)).setDataListeners(
                Arrays.asList((DataRouterListener) cache)
            );
    }

    @Test
    public void mergeTournamentGroupBase() throws Exception {
        SapiTournamentInfoEndpoint sapiTournament = new TestingDataProvider<SapiTournamentInfoEndpoint>(
            "test/rest/tournament_info.xml"
        )
            .getData();
        SportEventCi cacheItem = cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        TournamentCi tour40 = ((TournamentCi) cacheItem);
        assertNotNull(tour40);
        assertNotNull(sapiTournament);
        tour40.merge(sapiTournament, LOCALE);
        assertEquals(Urn.parse(sapiTournament.getTournament().getId()), tour40.getId());
        assertNotNull(sapiTournament.getGroups());
        List<GroupCi> groups = tour40.getGroups(LOCALES);
        assertNotNull(groups);

        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));
    }

    @Test
    public void mergeTournamentGroupRemoveById() throws Exception {
        SapiTournamentInfoEndpoint sapiTournament = new TestingDataProvider<SapiTournamentInfoEndpoint>(
            "test/rest/tournament_info.xml"
        )
            .getData();
        TournamentCi tour40 = (TournamentCi) cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

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
        SapiTournamentInfoEndpoint sapiTournament = new TestingDataProvider<SapiTournamentInfoEndpoint>(
            "test/rest/tournament_info.xml"
        )
            .getData();
        TournamentCi tour40 = (TournamentCi) cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

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
        SapiTournamentInfoEndpoint sapiTournament = new TestingDataProvider<SapiTournamentInfoEndpoint>(
            "test/rest/tournament_info.xml"
        )
            .getData();
        TournamentCi tour40 = (TournamentCi) cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

        // default
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // set group name
        sapiTournament.getGroups().getGroup().get(0).setId("1");
        sapiTournament.getGroups().getGroup().get(0).setName("Name1");
        tour40.merge(sapiTournament, LOCALE);
        verifyTournamentGroups(sapiTournament.getGroups().getGroup(), tour40.getGroups(LOCALES));

        // split group
        SapiTournamentGroup newGroup = new SapiTournamentGroup();
        newGroup.setId("2");
        newGroup.setName("Name2");
        newGroup.getCompetitor();
        int i = sapiTournament.getGroups().getGroup().get(0).getCompetitor().size() / 2;
        while (i > 0) {
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
        SapiTournamentInfoEndpoint sapiTournament = new TestingDataProvider<SapiTournamentInfoEndpoint>(
            "test/rest/tournament_info.xml"
        )
            .getData();
        TournamentCi tour40 = (TournamentCi) cache.getEventCacheItem(TOURNAMENT_EVENT_ID_40);

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

    private void verifyTournamentGroups(List<SapiTournamentGroup> sapiGroups, List<GroupCi> ciGroups) {
        if (sapiGroups == null || sapiGroups.isEmpty()) {
            assertTrue(ciGroups.isEmpty());
            return;
        }

        assertEquals(sapiGroups.size(), ciGroups.size());
        for (SapiTournamentGroup sapiGroup : sapiGroups) {
            if (!isNullOrEmpty(sapiGroup.getId())) {
                assertTrue(ciGroups.stream().anyMatch(m -> m.getId().equals(sapiGroup.getId())));
            }
            if (!isNullOrEmpty(sapiGroup.getName())) {
                assertTrue(ciGroups.stream().anyMatch(m -> m.getName().equals(sapiGroup.getName())));
            }
            GroupCi matchingGroup = ciGroups
                .stream()
                .filter(f ->
                    (!isNullOrEmpty(sapiGroup.getId()) && f.getId().equals(sapiGroup.getId())) ||
                    (!isNullOrEmpty(sapiGroup.getName()) && f.getName().equals(sapiGroup.getName())) ||
                    (
                        isNullOrEmpty(sapiGroup.getId()) &&
                        isNullOrEmpty(sapiGroup.getName()) &&
                        isNullOrEmpty(f.getId()) &&
                        isNullOrEmpty(f.getName())
                    )
                )
                .findFirst()
                .get();

            assertEquals(sapiGroup.getCompetitor().size(), matchingGroup.getCompetitorIds().size());

            for (SapiTeam sapiCompetitor : sapiGroup.getCompetitor()) {
                assertTrue(matchingGroup.getCompetitorIds().contains(Urn.parse(sapiCompetitor.getId())));
            }
        }
    }

    private static SdkInternalConfiguration configurationWithAnyLanguageThrowingOnErrors() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        when(mock.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        return mock;
    }

    private boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
