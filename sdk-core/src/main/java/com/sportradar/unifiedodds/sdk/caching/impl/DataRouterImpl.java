/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterListener;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implements methods used to trigger API fetches
 */
public class DataRouterImpl implements DataRouter {
    private final static Logger logger = LoggerFactory.getLogger(DataRouterImpl.class);

    /**
     * A {@link List} of listeners interested in the router fetches
     */
    private List<DataRouterListener> dataListeners;

    @Override
    public void onSummaryFetched(URN requestedId, Object data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(requestedId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        if (data instanceof SAPITournamentInfoEndpoint) {
            SAPITournamentInfoEndpoint endpoint = (SAPITournamentInfoEndpoint) data;

            URN trnId = URN.parse(endpoint.getTournament().getId());
            URN seasonId = endpoint.getSeason() == null ? null : URN.parse(endpoint.getSeason().getId());
            dataListeners.forEach(l -> l.onTournamentInfoEndpointFetched(requestedId, trnId, seasonId, endpoint, locale, requester));
            Optional.ofNullable(endpoint.getCompetitors()).ifPresent(c -> dispatchTournamentCompetitors(c.getCompetitor(), locale, requester));
            Optional.ofNullable(endpoint.getGroups())
                    .ifPresent(g -> g.getGroup().forEach(gr ->
                            dispatchTournamentCompetitors(gr.getCompetitor(), locale, requester)
                    ));
        } else if (data instanceof SAPIMatchSummaryEndpoint) {
            SAPIMatchSummaryEndpoint endpoint = (SAPIMatchSummaryEndpoint) data;

            URN matchId = URN.parse(endpoint.getSportEvent().getId());
            dataListeners.forEach(l -> l.onMatchSummaryEndpointFetched(matchId, endpoint, locale, requester));
            dispatchTournament(endpoint.getSportEvent().getTournament(), locale);
            Optional.ofNullable(endpoint.getSportEvent().getCompetitors()).ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
            Optional.ofNullable(endpoint.getSportEventStatus()).ifPresent(c -> onSportEventStatusFetched(URN.parse(endpoint.getSportEvent().getId()), new SportEventStatusDTO(c, endpoint.getStatistics(), provideHomeAway(endpoint.getSportEvent())), endpoint.getSportEvent().getStatus(), "SAPIMatchSummaryEndpoint"));
        } else if (data instanceof SAPIStageSummaryEndpoint) {
            SAPIStageSummaryEndpoint endpoint = ((SAPIStageSummaryEndpoint) data);

            URN stageId = URN.parse(endpoint.getSportEvent().getId());
            dataListeners.forEach(l -> l.onStageSummaryEndpointFetched(stageId, endpoint, locale, requester));
            dispatchTournament(endpoint.getSportEvent().getTournament(), locale);
            Optional.ofNullable(endpoint.getSportEvent().getCompetitors()).ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
            Optional.ofNullable(endpoint.getSportEventStatus()).ifPresent(c -> onSportEventStatusFetched(URN.parse(endpoint.getSportEvent().getId()), new SportEventStatusDTO(c), endpoint.getSportEvent().getStatus(), "SAPIStageSummaryEndpoint"));
        } else {
            logger.warn("Received unsupported summary endpoint object[{}], requestedId:'{}'", data.getClass(), requestedId);
        }
    }

    @Override
    public void onFixtureFetched(URN fixtureId, SAPIFixture fixture, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(fixtureId);
        Preconditions.checkNotNull(fixture);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onFixtureFetched(fixtureId, fixture, locale, requester));

        dispatchTournament(fixture.getTournament(), locale);
        Optional.ofNullable(fixture.getCompetitors()).ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
    }

    @Override
    public void onDrawSummaryFetched(URN drawId, SAPIDrawSummary endpoint, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(drawId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(requester);

        if (endpoint.getDrawFixture().getLottery() != null) {
            URN lotteryId = URN.parse(endpoint.getDrawFixture().getLottery().getId());
            dataListeners.forEach(l -> l.onLotteryFetched(lotteryId, endpoint.getDrawFixture().getLottery(), locale, requester));
        }

        dataListeners.forEach(l -> l.onDrawSummaryEndpointFetched(drawId, endpoint, locale, requester));
    }

    @Override
    public void onDrawFixtureFetched(URN drawId, SAPIDrawFixture endpoint, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(drawId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(requester);

        if (endpoint.getLottery() != null) {
            URN lotteryId = URN.parse(endpoint.getLottery().getId());
            dataListeners.forEach(l -> l.onLotteryFetched(lotteryId, endpoint.getLottery(), locale, requester));
        }

        dataListeners.forEach(l -> l.onDrawFixtureFetched(drawId, endpoint, locale, requester));
    }

    @Override
    public void onAllTournamentsListFetched(SAPITournamentsEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint.getTournament().forEach(tournament -> {
            URN trnId = URN.parse(tournament.getId());
            dataListeners.forEach(l -> l.onTournamentExtendedFetched(trnId, tournament, locale));
        });
    }

    @Override
    public void onAllLotteriesListFetched(SAPILotteries endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint.getLottery().forEach(lottery -> {
            URN lotteryId = URN.parse(lottery.getId());
            dataListeners.forEach(l -> l.onLotteryFetched(lotteryId, lottery, locale, null));
        });
    }

    @Override
    public void onTournamentScheduleFetched(Object endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        if (endpoint instanceof SAPITournamentSchedule) {
            dispatchTournamentSchedule((SAPITournamentSchedule) endpoint, locale);
        } else if (endpoint instanceof SAPIRaceScheduleEndpoint) {
            dispatchTournamentSchedule((SAPIRaceScheduleEndpoint) endpoint, locale);
        } else {
            logger.warn("Received unsupported tournament schedule endpoint object[{}], locale:{}", endpoint.getClass(), locale);
        }
    }

    @Override
    public void onLotteryScheduleFetched(SAPILotterySchedule endpoint, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        SAPILottery lottery = endpoint.getLottery();
        URN parse = URN.parse(lottery.getId());
        dataListeners.forEach(l -> l.onLotteryFetched(parse, lottery, locale, requester));

        if (endpoint.getDrawEvents() != null && endpoint.getDrawEvents().getDrawEvent() != null) {
            endpoint.getDrawEvents().getDrawEvent().forEach(draw -> {
                URN drawId = URN.parse(draw.getId());
                dataListeners.forEach(l -> l.onDrawFetched(drawId, draw, locale, requester));
            });
        }
    }

    @Override
    public void onDateScheduleFetched(SAPIScheduleEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        dispatchSportEvents(endpoint.getSportEvent(), locale);
    }

    @Override
    public void onSportsListFetched(SAPISportsEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint.getSport().forEach(sport ->
            dataListeners.forEach(l -> l.onSportFetched(URN.parse(sport.getId()), sport, locale))
        );
    }

    @Override
    public void onPlayerFetched(URN playerId, SAPIPlayerExtended data, Locale locale, CacheItem requester, URN competitorId) {
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onPlayerFetched(playerId, data, locale, requester, competitorId));
    }

    @Override
    public void onCompetitorFetched(URN competitorId, SAPICompetitorProfileEndpoint data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(competitorId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        SAPIRaceDriverProfile raceDriverProfile = data.getRaceDriverProfile();
        if (raceDriverProfile != null) {
            SAPITeam raceDriver = raceDriverProfile.getRaceDriver();
            if (raceDriver != null) {
                dataListeners.forEach(l -> l.onTeamFetched(URN.parse(raceDriver.getId()), raceDriver, locale, requester));
            }

            SAPITeam raceTeam = raceDriverProfile.getRaceTeam();
            if (raceTeam != null) {
                dataListeners.forEach(l -> l.onTeamFetched(URN.parse(raceTeam.getId()), raceTeam, locale, requester));
            }
        }

        Optional.ofNullable(data.getPlayers()).ifPresent(c ->
                c.getPlayer().forEach(p ->
                    this.onPlayerFetched(URN.parse(p.getId()), p, locale, requester, URN.parse(data.getCompetitor().getId()))
                ));

        dataListeners.forEach(l -> l.onCompetitorFetched(competitorId, data, locale, requester));
    }

    @Override
    public void onSimpleTeamFetched(URN competitorId, SAPISimpleTeamProfileEndpoint data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(competitorId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onSimpleTeamFetched(competitorId, data, locale, requester));
    }

    @Override
    public void onTournamentSeasonsFetched(URN tournamentId, SAPITournamentSeasons data, Locale locale) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onTournamentFetched(tournamentId, data.getTournament(), locale));
    }

    @Override
    public void onMatchTimelineFetched(URN matchId, SAPIMatchTimelineEndpoint endpoint, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(matchId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onMatchTimelineFetched(matchId, endpoint, locale, requester));
        dispatchTournament(endpoint.getSportEvent().getTournament(), locale);
        Optional.ofNullable(endpoint.getSportEvent().getCompetitors()).ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
        Optional.ofNullable(endpoint.getSportEventStatus()).ifPresent(c -> onSportEventStatusFetched(URN.parse(endpoint.getSportEvent().getId()), new SportEventStatusDTO(c, null, provideHomeAway(endpoint.getSportEvent())), endpoint.getSportEvent().getStatus(), "SAPIMatchTimelineEndpoint"));
    }

    @Override
    public void onSportEventStatusFetched(URN eventId, SportEventStatusDTO data, String statusOnEvent, String source) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkNotNull(data);

        dataListeners.forEach(l -> l.onSportEventStatusFetched(eventId, data, statusOnEvent, source));
    }

    @Override
    public void onSportCategoriesFetched(SAPISportCategoriesEndpoint endpoint, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);
        URN sportId = URN.parse(endpoint.getSport().getId());
        dataListeners.forEach(l -> l.onSportCategoriesFetched(sportId, endpoint, locale, requester));
    }

    @Override
    public void onAvailableSelectionsFetched(URN id, CAPIAvailableSelections availableSelections) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(availableSelections);
        dataListeners.forEach(l -> l.onAvailableSelectionsFetched(id, availableSelections));
    }

    @Override
    public void onCalculateProbabilityFetched(List<Selection> selections, CAPICalculationResponse calculation) {
        Preconditions.checkNotNull(selections);
        Preconditions.checkNotNull(calculation);
        dataListeners.forEach(l -> l.onCalculateProbabilityFetched(selections, calculation));
    }

    @Override
    public void onListSportEventsFetched(SAPIScheduleEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        dispatchSportEvents(endpoint.getSportEvent(), locale);
    }

    @Override
    public void onSportTournamentsFetched(URN sportId, SAPISportTournamentsEndpoint endpoint, Locale locale)
    {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint.getTournaments().getTournament().forEach(tournament ->
                dataListeners.forEach(l -> l.onTournamentFetched(URN.parse(tournament.getId()), tournament, locale))
        );
    }

    private void dispatchTournamentSchedule(SAPIRaceScheduleEndpoint endpoint, Locale locale) {
        if (endpoint.getTournament() != null) {
            URN trnId = URN.parse(endpoint.getTournament().getId());
            dataListeners.forEach(l -> l.onTournamentFetched(trnId, endpoint.getTournament(), locale));
        }

        if (endpoint.getSportEvents() != null && endpoint.getSportEvents().getSportEvent() != null) {
            dispatchChildSportEvents(endpoint.getSportEvents().getSportEvent(), locale);
        }
    }

    private void dispatchTournamentSchedule(SAPITournamentSchedule endpoint, Locale locale) {
        if (endpoint.getTournament() != null) {
            endpoint.getTournament().forEach(t -> {
                URN trnId = URN.parse(t.getId());
                dataListeners.forEach(l -> l.onTournamentExtendedFetched(trnId, t, locale));
            });
        }

        if (endpoint.getSportEvents() != null) {
            endpoint.getSportEvents().forEach(seWrapper -> dispatchSportEvents(seWrapper.getSportEvent(), locale));
        }
    }

    private void dispatchSportEvents(List<SAPISportEvent> sportEvents, Locale locale) {
        Preconditions.checkNotNull(locale);

        if (sportEvents == null || sportEvents.isEmpty()) {
            return;
        }

        sportEvents.forEach(se -> {
                dataListeners.forEach(l -> l.onSportEventFetched(URN.parse(se.getId()), se, locale)
                );
                if (se.getTournament() != null) {
                    dispatchTournament(se.getTournament(), locale);
                }
                Optional.ofNullable(se.getCompetitors()).ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, null));
            }
        );
    }

    private void dispatchTournament(SAPITournament tournament, Locale locale) {
        Preconditions.checkNotNull(tournament);
        Preconditions.checkNotNull(locale);

        URN tournamentId = URN.parse(tournament.getId());
        dataListeners.forEach(l -> l.onTournamentFetched(tournamentId, tournament, locale));
    }

    private void dispatchChildSportEvents(List<SAPISportEventChildren.SAPISportEvent> sportEvents, Locale locale) {
        Preconditions.checkNotNull(sportEvents);
        Preconditions.checkNotNull(locale);

        sportEvents.forEach(se ->
                dataListeners.forEach(l ->
                        l.onChildSportEventFetched(URN.parse(se.getId()), se, locale)
                )
        );
    }

    private void dispatchEventCompetitors(List<SAPITeamCompetitor> competitors, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(competitors);
        Preconditions.checkNotNull(dataLocale);

        competitors.forEach(c -> {
            URN parsedId = URN.parse(c.getId());
            dataListeners.forEach(l -> l.onTeamFetched(parsedId, c, dataLocale, requester));
        });
    }

    private void dispatchTournamentCompetitors(List<SAPITeam> competitors, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(competitors);
        Preconditions.checkNotNull(dataLocale);

        competitors.forEach(c -> {
            URN parsedId = URN.parse(c.getId());
            dataListeners.forEach(l -> l.onTeamFetched(parsedId, c, dataLocale, requester));
        });
    }

    /**
     * Provides valid home away competitor identifiers. This method returns valid identifiers only for events of type match.
     *
     * @param se the sport event from which the valid competitors should be provided
     * @return a map containing valid home/away competitor identifiers
     */
    private Map<HomeAway, String> provideHomeAway(SAPISportEvent se) {
        Preconditions.checkNotNull(se);

        if (se.getCompetitors() == null) {
            return null;
        }

        if (se.getCompetitors().getCompetitor() != null && se.getCompetitors().getCompetitor().size() != 2) {
            return null;
        }

        List<SAPITeamCompetitor> competitors = se.getCompetitors().getCompetitor();

        SAPITeamCompetitor home = competitors.stream().filter(c -> c.getQualifier().equals("home")).findAny().orElse(null);
        SAPITeamCompetitor away = competitors.stream().filter(c -> c.getQualifier().equals("away")).findAny().orElse(null);

        if (home == null || away == null) {
            logger.warn("Handling provideHomeAway with invalid competitors data. SportEvent:{}", se.getId());
            return null;
        }

        Map<HomeAway, String> result = new HashMap<>(2);
        result.put(HomeAway.Home, home.getId());
        result.put(HomeAway.Away, away.getId());

        return result;
    }

    public void setDataListeners(List<DataRouterListener> dataListeners) {
        Preconditions.checkNotNull(dataListeners);
        this.dataListeners = dataListeners;
    }
}