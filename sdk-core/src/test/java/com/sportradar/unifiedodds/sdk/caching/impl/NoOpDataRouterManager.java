/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import com.sportradar.uf.sportsapi.datamodel.SAPIMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;
import com.sportradar.unifiedodds.sdk.custombetentities.CalculationFilter;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.URN;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoOpDataRouterManager implements DataRouterManager {

    @Override
    public void requestSummaryEndpoint(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestFixtureEndpoint(Locale locale, URN id, boolean useCachedProvider, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestDrawSummary(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestDrawFixture(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestAllTournamentsForAllSportsEndpoint(Locale locale) throws CommunicationException {}

    @Override
    public void requestAllSportsEndpoint(Locale locale) throws CommunicationException {}

    @Override
    public List<URN> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestEventsFor(Locale locale, URN tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestEventsFor(Locale locale, Date date) throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestLotterySchedule(Locale locale, URN lotteryId, CacheItem requester)
        throws CommunicationException {
        return null;
    }

    @Override
    public void requestPlayerProfileEndpoint(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestCompetitorEndpoint(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public void requestSimpleTeamEndpoint(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public List<URN> requestSeasonsFor(Locale locale, URN tournamentId) throws CommunicationException {
        return null;
    }

    @Override
    public SAPIMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {
        return null;
    }

    @Override
    public void requestSportCategoriesEndpoint(Locale locale, URN id, CacheItem requester)
        throws CommunicationException {}

    @Override
    public AvailableSelections requestAvailableSelections(URN id) throws CommunicationException {
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
    public List<FixtureChange> requestFixtureChanges(Date after, URN sportId, Locale locale)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<ResultChange> requestResultChanges(Date after, URN sportId, Locale locale)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestListSportEvents(Locale locale, int startIndex, int limit)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<URN> requestAvailableTournamentsFor(Locale locale, URN sportId)
        throws CommunicationException {
        return null;
    }

    @Override
    public List<PeriodStatus> requestPeriodSummary(
        URN id,
        Locale locale,
        List<URN> competitorIds,
        List<Integer> periods
    ) throws CommunicationException {
        return null;
    }

    @Override
    public void close() {}
}
