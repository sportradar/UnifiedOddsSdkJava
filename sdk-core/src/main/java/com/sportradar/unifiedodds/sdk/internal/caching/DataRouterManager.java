/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created on 26/10/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "OverloadMethodsDeclarationOrder" })
public interface DataRouterManager {
    void requestSummaryEndpoint(Locale locale, Urn id, CacheItem requester) throws CommunicationException;

    void requestSummaryEndpoint(Locale locale, Urn id, CacheItem requester, RequestOptions requestOptions)
        throws CommunicationException;

    void requestFixtureEndpoint(Locale locale, Urn id, boolean useCachedProvider, CacheItem requester)
        throws CommunicationException;

    void requestDrawSummary(Locale locale, Urn id, CacheItem requester) throws CommunicationException;

    void requestDrawFixture(Locale locale, Urn id, CacheItem requester) throws CommunicationException;

    void requestAllTournamentsForAllSportsEndpoint(Locale locale) throws CommunicationException;

    void requestAllSportsEndpoint(Locale locale) throws CommunicationException;

    List<Urn> requestAllLotteriesEndpoint(Locale locale, Boolean requireResult) throws CommunicationException;

    List<Urn> requestEventsFor(Locale locale, Urn tournamentId) throws CommunicationException;

    List<Urn> requestLotterySchedule(Locale locale, Urn lotteryId, CacheItem requester)
        throws CommunicationException;

    List<Urn> requestEventsFor(Locale locale, Date date) throws CommunicationException;

    void requestPlayerProfileEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException;

    void requestCompetitorEndpoint(Locale locale, Urn id, CacheItem requester) throws CommunicationException;

    void requestSimpleTeamEndpoint(Locale locale, Urn id, CacheItem requester) throws CommunicationException;

    List<Urn> requestSeasonsFor(Locale locale, Urn tournamentId) throws CommunicationException;

    SapiMatchTimelineEndpoint requestEventTimelineEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException;

    void requestSportCategoriesEndpoint(Locale locale, Urn id, CacheItem requester)
        throws CommunicationException;

    AvailableSelections requestAvailableSelections(Urn id) throws CommunicationException;

    Calculation requestCalculateProbability(List<Selection> selections) throws CommunicationException;

    CalculationFilter requestCalculateProbabilityFilter(List<Selection> selections)
        throws CommunicationException;

    List<FixtureChange> requestFixtureChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException;

    List<ResultChange> requestResultChanges(Date after, Urn sportId, Locale locale)
        throws CommunicationException;

    List<Urn> requestListSportEvents(Locale locale, int startIndex, int limit) throws CommunicationException;

    List<Urn> requestAvailableTournamentsFor(Locale locale, Urn sportId) throws CommunicationException;

    List<PeriodStatus> requestPeriodSummary(
        Urn id,
        Locale locale,
        List<Urn> competitorIds,
        List<Integer> periods
    ) throws CommunicationException;

    void close();
}
