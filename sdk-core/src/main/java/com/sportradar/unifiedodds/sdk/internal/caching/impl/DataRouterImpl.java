/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import com.google.common.base.Preconditions;
import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.internal.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterListener;
import com.sportradar.unifiedodds.sdk.internal.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements methods used to trigger API fetches
 */
@SuppressWarnings(
    {
        "BooleanExpressionComplexity",
        "ClassFanOutComplexity",
        "ConstantName",
        "HiddenField",
        "LambdaBodyLength",
        "LineLength",
        "MethodLength",
        "UnnecessaryParentheses",
    }
)
public class DataRouterImpl implements DataRouter {

    private static final Logger logger = LoggerFactory.getLogger(DataRouterImpl.class);

    /**
     * A {@link List} of listeners interested in the router fetches
     */
    private List<DataRouterListener> dataListeners;

    @Override
    public void onSummaryFetched(Urn requestedId, Object data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(requestedId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        if (data instanceof SapiTournamentInfoEndpoint) {
            SapiTournamentInfoEndpoint endpoint = (SapiTournamentInfoEndpoint) data;

            Urn trnId = Urn.parse(endpoint.getTournament().getId());
            Urn seasonId = endpoint.getSeason() == null ? null : Urn.parse(endpoint.getSeason().getId());
            dataListeners.forEach(l ->
                l.onTournamentInfoEndpointFetched(requestedId, trnId, seasonId, endpoint, locale, requester)
            );
            Optional
                .ofNullable(endpoint.getTournament().getCompetitors())
                .ifPresent(c -> dispatchTournamentCompetitors(c.getCompetitor(), locale, requester));
            Optional
                .ofNullable(endpoint.getCompetitors())
                .ifPresent(c -> dispatchTournamentCompetitors(c.getCompetitor(), locale, requester));
            Optional
                .ofNullable(endpoint.getGroups())
                .ifPresent(g ->
                    g
                        .getGroup()
                        .forEach(gr -> dispatchTournamentCompetitors(gr.getCompetitor(), locale, requester))
                );
        } else if (data instanceof SapiMatchSummaryEndpoint) {
            SapiMatchSummaryEndpoint endpoint = (SapiMatchSummaryEndpoint) data;

            Urn matchId = Urn.parse(endpoint.getSportEvent().getId());
            dataListeners.forEach(l -> l.onMatchSummaryEndpointFetched(matchId, endpoint, locale, requester));
            dispatchTournament(endpoint.getSportEvent().getTournament(), locale);
            Optional
                .ofNullable(endpoint.getSportEvent().getCompetitors())
                .ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
            Optional
                .ofNullable(endpoint.getSportEventStatus())
                .ifPresent(c ->
                    onSportEventStatusFetched(
                        Urn.parse(endpoint.getSportEvent().getId()),
                        new SportEventStatusDto(
                            c,
                            endpoint.getStatistics(),
                            provideHomeAway(endpoint.getSportEvent())
                        ),
                        endpoint.getSportEvent().getStatus(),
                        "SapiMatchSummaryEndpoint"
                    )
                );
        } else if (data instanceof SapiStageSummaryEndpoint) {
            SapiStageSummaryEndpoint endpoint = ((SapiStageSummaryEndpoint) data);

            Urn stageId = Urn.parse(endpoint.getSportEvent().getId());
            dataListeners.forEach(l -> l.onStageSummaryEndpointFetched(stageId, endpoint, locale, requester));
            dispatchTournament(endpoint.getSportEvent().getTournament(), locale);
            Optional
                .ofNullable(endpoint.getSportEvent().getCompetitors())
                .ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
            Optional
                .ofNullable(endpoint.getSportEventStatus())
                .ifPresent(c ->
                    onSportEventStatusFetched(
                        Urn.parse(endpoint.getSportEvent().getId()),
                        new SportEventStatusDto(c),
                        endpoint.getSportEvent().getStatus(),
                        "SapiStageSummaryEndpoint"
                    )
                );
        } else {
            logger.warn(
                "Received unsupported summary endpoint object[{}], requestedId:'{}'",
                data.getClass(),
                requestedId
            );
        }
    }

    @Override
    public void onFixtureFetched(Urn fixtureId, SapiFixture fixture, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(fixtureId);
        Preconditions.checkNotNull(fixture);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onFixtureFetched(fixtureId, fixture, locale, requester));

        if (fixture.getTournament() != null) {
            dispatchTournament(fixture.getTournament(), locale);
        }
        Optional
            .ofNullable(fixture.getCompetitors())
            .ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
    }

    @Override
    public void onDrawSummaryFetched(
        Urn drawId,
        SapiDrawSummary endpoint,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(drawId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(requester);

        if (endpoint.getDrawFixture().getLottery() != null) {
            Urn lotteryId = Urn.parse(endpoint.getDrawFixture().getLottery().getId());
            dataListeners.forEach(l ->
                l.onLotteryFetched(lotteryId, endpoint.getDrawFixture().getLottery(), locale, requester)
            );
        }

        dataListeners.forEach(l -> l.onDrawSummaryEndpointFetched(drawId, endpoint, locale, requester));
    }

    @Override
    public void onDrawFixtureFetched(
        Urn drawId,
        SapiDrawFixture endpoint,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(drawId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(requester);

        if (endpoint.getLottery() != null) {
            Urn lotteryId = Urn.parse(endpoint.getLottery().getId());
            dataListeners.forEach(l -> l.onLotteryFetched(lotteryId, endpoint.getLottery(), locale, requester)
            );
        }

        dataListeners.forEach(l -> l.onDrawFixtureFetched(drawId, endpoint, locale, requester));
    }

    @Override
    public void onAllTournamentsListFetched(SapiTournamentsEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint
            .getTournament()
            .forEach(tournament -> {
                Urn trnId = Urn.parse(tournament.getId());
                dataListeners.forEach(l -> l.onTournamentExtendedFetched(trnId, tournament, locale));
            });
    }

    @Override
    public void onAllLotteriesListFetched(SapiLotteries endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint
            .getLottery()
            .forEach(lottery -> {
                Urn lotteryId = Urn.parse(lottery.getId());
                dataListeners.forEach(l -> l.onLotteryFetched(lotteryId, lottery, locale, null));
            });
    }

    @Override
    public void onTournamentScheduleFetched(Object endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        if (endpoint instanceof SapiTournamentSchedule) {
            dispatchTournamentSchedule((SapiTournamentSchedule) endpoint, locale);
        } else if (endpoint instanceof SapiRaceScheduleEndpoint) {
            dispatchTournamentSchedule((SapiRaceScheduleEndpoint) endpoint, locale);
        } else {
            logger.warn(
                "Received unsupported tournament schedule endpoint object[{}], locale:{}",
                endpoint.getClass(),
                locale
            );
        }
    }

    @Override
    public void onLotteryScheduleFetched(SapiLotterySchedule endpoint, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        SapiLottery lottery = endpoint.getLottery();
        Urn parse = Urn.parse(lottery.getId());
        dataListeners.forEach(l -> l.onLotteryFetched(parse, lottery, locale, requester));

        if (endpoint.getDrawEvents() != null && endpoint.getDrawEvents().getDrawEvent() != null) {
            endpoint
                .getDrawEvents()
                .getDrawEvent()
                .forEach(draw -> {
                    Urn drawId = Urn.parse(draw.getId());
                    dataListeners.forEach(l -> l.onDrawFetched(drawId, draw, locale, requester));
                });
        }
    }

    @Override
    public void onDateScheduleFetched(SapiScheduleEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        dispatchSportEvents(endpoint.getSportEvent(), locale);
    }

    @Override
    public void onSportsListFetched(SapiSportsEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        endpoint
            .getSport()
            .forEach(sport ->
                dataListeners.forEach(l -> l.onSportFetched(Urn.parse(sport.getId()), sport, locale))
            );
    }

    @Override
    public void onPlayerFetched(
        Urn playerId,
        SapiPlayerExtended data,
        Locale locale,
        CacheItem requester,
        Urn competitorId
    ) {
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onPlayerFetched(playerId, data, locale, requester, competitorId));
    }

    @Override
    public void onCompetitorFetched(
        Urn competitorId,
        SapiCompetitorProfileEndpoint data,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(competitorId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        SapiRaceDriverProfile raceDriverProfile = data.getRaceDriverProfile();
        if (raceDriverProfile != null) {
            SapiTeam raceDriver = raceDriverProfile.getRaceDriver();
            if (raceDriver != null) {
                dataListeners.forEach(l ->
                    l.onTeamFetched(Urn.parse(raceDriver.getId()), raceDriver, locale, requester)
                );
            }

            SapiTeam raceTeam = raceDriverProfile.getRaceTeam();
            if (raceTeam != null) {
                dataListeners.forEach(l ->
                    l.onTeamFetched(Urn.parse(raceTeam.getId()), raceTeam, locale, requester)
                );
            }
        }

        Optional
            .ofNullable(data.getPlayers())
            .ifPresent(c ->
                c
                    .getPlayer()
                    .forEach(p ->
                        this.onPlayerFetched(
                                Urn.parse(p.getId()),
                                p,
                                locale,
                                requester,
                                Urn.parse(data.getCompetitor().getId())
                            )
                    )
            );

        dataListeners.forEach(l -> l.onCompetitorFetched(competitorId, data, locale, requester));
    }

    @Override
    public void onSimpleTeamFetched(
        Urn competitorId,
        SapiSimpleTeamProfileEndpoint data,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(competitorId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onSimpleTeamFetched(competitorId, data, locale, requester));
    }

    @Override
    public void onTournamentSeasonsFetched(Urn tournamentId, SapiTournamentSeasons data, Locale locale) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onTournamentFetched(tournamentId, data.getTournament(), locale));
    }

    @Override
    public void onMatchTimelineFetched(
        Urn matchId,
        SapiMatchTimelineEndpoint endpoint,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(matchId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        dataListeners.forEach(l -> l.onMatchTimelineFetched(matchId, endpoint, locale, requester));
        dispatchTournament(endpoint.getSportEvent().getTournament(), locale);
        Optional
            .ofNullable(endpoint.getSportEvent().getCompetitors())
            .ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, requester));
        Optional
            .ofNullable(endpoint.getSportEventStatus())
            .ifPresent(c ->
                onSportEventStatusFetched(
                    Urn.parse(endpoint.getSportEvent().getId()),
                    new SportEventStatusDto(c, null, provideHomeAway(endpoint.getSportEvent())),
                    endpoint.getSportEvent().getStatus(),
                    "SapiMatchTimelineEndpoint"
                )
            );
    }

    @Override
    public void onSportEventStatusFetched(
        Urn eventId,
        SportEventStatusDto statusDto,
        String statusOnEvent,
        String source
    ) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkNotNull(statusDto);

        dataListeners.forEach(l -> l.onSportEventStatusFetched(eventId, statusDto, statusOnEvent, source));
    }

    @Override
    public void onSportCategoriesFetched(
        SapiSportCategoriesEndpoint endpoint,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);
        Urn sportId = Urn.parse(endpoint.getSport().getId());
        dataListeners.forEach(l -> l.onSportCategoriesFetched(sportId, endpoint, locale, requester));
    }

    @Override
    public void onAvailableSelectionsFetched(Urn id, CapiAvailableSelections availableSelections) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(availableSelections);
        dataListeners.forEach(l -> l.onAvailableSelectionsFetched(id, availableSelections));
    }

    @Override
    public void onCalculateProbabilityFetched(
        List<Selection> selections,
        CapiCalculationResponse calculation
    ) {
        Preconditions.checkNotNull(selections);
        Preconditions.checkNotNull(calculation);
        dataListeners.forEach(l -> l.onCalculateProbabilityFetched(selections, calculation));
    }

    @Override
    public void onCalculateProbabilityFilterFetched(
        List<Selection> selections,
        CapiFilteredCalculationResponse calculation
    ) {
        Preconditions.checkNotNull(selections);
        Preconditions.checkNotNull(calculation);
        dataListeners.forEach(l -> l.onCalculateProbabilityFilterFetched(selections, calculation));
    }

    @Override
    public void onListSportEventsFetched(SapiScheduleEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        dispatchSportEvents(endpoint.getSportEvent(), locale);
    }

    @Override
    public void onSportTournamentsFetched(Urn sportId, SapiSportTournamentsEndpoint endpoint, Locale locale) {
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkNotNull(locale);

        if (
            endpoint == null ||
            endpoint.getTournaments() == null ||
            endpoint.getTournaments().getTournament() == null ||
            endpoint.getTournaments().getTournament().isEmpty()
        ) {
            return;
        }

        endpoint
            .getTournaments()
            .getTournament()
            .forEach(tournament ->
                dataListeners.forEach(l ->
                    l.onTournamentFetched(Urn.parse(tournament.getId()), tournament, locale)
                )
            );
    }

    private void dispatchTournamentSchedule(SapiRaceScheduleEndpoint endpoint, Locale locale) {
        if (endpoint.getTournament() != null) {
            Urn trnId = Urn.parse(endpoint.getTournament().getId());
            dataListeners.forEach(l -> l.onTournamentFetched(trnId, endpoint.getTournament(), locale));
        }

        if (endpoint.getSportEvents() != null && endpoint.getSportEvents().getSportEvent() != null) {
            dispatchChildSportEvents(endpoint.getSportEvents().getSportEvent(), locale);
        }
    }

    private void dispatchTournamentSchedule(SapiTournamentSchedule endpoint, Locale locale) {
        if (endpoint.getTournament() != null) {
            endpoint
                .getTournament()
                .forEach(t -> {
                    Urn trnId = Urn.parse(t.getId());
                    dataListeners.forEach(l -> l.onTournamentExtendedFetched(trnId, t, locale));
                });
        }

        if (endpoint.getSportEvents() != null) {
            endpoint
                .getSportEvents()
                .forEach(seWrapper -> dispatchSportEvents(seWrapper.getSportEvent(), locale));
        }
    }

    private void dispatchSportEvents(List<SapiSportEvent> sportEvents, Locale locale) {
        Preconditions.checkNotNull(locale);

        if (sportEvents == null || sportEvents.isEmpty()) {
            return;
        }

        sportEvents.forEach(se -> {
            dataListeners.forEach(l -> l.onSportEventFetched(Urn.parse(se.getId()), se, locale));
            if (se.getTournament() != null) {
                dispatchTournament(se.getTournament(), locale);
            }
            Optional
                .ofNullable(se.getCompetitors())
                .ifPresent(c -> dispatchEventCompetitors(c.getCompetitor(), locale, null));
        });
    }

    private void dispatchTournament(SapiTournament tournament, Locale locale) {
        Preconditions.checkNotNull(tournament);
        Preconditions.checkNotNull(locale);

        Urn tournamentId = Urn.parse(tournament.getId());
        dataListeners.forEach(l -> l.onTournamentFetched(tournamentId, tournament, locale));
    }

    private void dispatchChildSportEvents(
        List<SapiSportEventChildren.SapiSportEvent> sportEvents,
        Locale locale
    ) {
        Preconditions.checkNotNull(sportEvents);
        Preconditions.checkNotNull(locale);

        sportEvents.forEach(se ->
            dataListeners.forEach(l -> l.onChildSportEventFetched(Urn.parse(se.getId()), se, locale))
        );
    }

    private void dispatchEventCompetitors(
        List<SapiTeamCompetitor> competitors,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(competitors);
        Preconditions.checkNotNull(dataLocale);

        competitors.forEach(c -> {
            Urn parsedId = Urn.parse(c.getId());
            dataListeners.forEach(l -> l.onTeamFetched(parsedId, c, dataLocale, requester));
        });
    }

    private void dispatchTournamentCompetitors(
        List<SapiTeam> competitors,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(competitors);
        Preconditions.checkNotNull(dataLocale);

        competitors.forEach(c -> {
            Urn parsedId = Urn.parse(c.getId());
            dataListeners.forEach(l -> l.onTeamFetched(parsedId, c, dataLocale, requester));
        });
    }

    /**
     * Provides valid home away competitor identifiers. This method returns valid identifiers only for events of type match.
     *
     * @param se the sport event from which the valid competitors should be provided
     * @return a map containing valid home/away competitor identifiers
     */
    private Map<HomeAway, String> provideHomeAway(SapiSportEvent se) {
        Preconditions.checkNotNull(se);

        if (se.getCompetitors() == null) {
            return null;
        }

        if (se.getCompetitors().getCompetitor() != null && se.getCompetitors().getCompetitor().size() != 2) {
            return null;
        }

        List<SapiTeamCompetitor> competitors = se.getCompetitors().getCompetitor();

        SapiTeamCompetitor home = competitors
            .stream()
            .filter(c -> c.getQualifier().equals("home"))
            .findAny()
            .orElse(null);
        SapiTeamCompetitor away = competitors
            .stream()
            .filter(c -> c.getQualifier().equals("away"))
            .findAny()
            .orElse(null);

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
