/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoOpDataRouterManager implements DataRouterManager {

    @Override
    public void requestSummaryEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestFixtureEndpoint(Locale locale, Urn id, boolean useCachedProvider, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestDrawSummary(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestDrawFixture(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestAllTournamentsForAllSportsEndpoint(Locale locale) throws CommunicationException {}

    @Override
    public void requestAllSportsEndpoint(Locale locale) throws CommunicationException {}

    @Override
    public List<Urn> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestEventsFor(Locale locale, Urn tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestEventsFor(Locale locale, Date date) throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestLotterySchedule(Locale locale, Urn lotteryId, CacheItem requester)
        throws CommunicationException {
        return null;
    }

    @Override
    public void requestPlayerProfileEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestCompetitorEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestSimpleTeamEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public List<Urn> requestSeasonsFor(Locale locale, Urn tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public SapiMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {
        return null;
    }

    @Override
    public void requestSportCategoriesEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public AvailableSelections requestAvailableSelections(Urn id) throws CommunicationException {
        return null;
    }

    @Override
    public Calculation requestCalculateProbability(List<Selection> selections) throws CommunicationException {
        return null;
    }

    @Override
    public CalculationFilter requestCalculateProbabilityFilter(List<Selection> selections)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<FixtureChange> requestFixtureChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<ResultChange> requestResultChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestListSportEvents(Locale locale, int startIndex, int limit)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<Urn> requestAvailableTournamentsFor(Locale locale, Urn sportId)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<PeriodStatus> requestPeriodSummary(
        Urn id,
        Locale locale,
        List<Urn> competitorIds,
        List<Integer> periods
    ) throws CommunicationException {
        return null;
    }

    @Override
    public void close() {}
}
