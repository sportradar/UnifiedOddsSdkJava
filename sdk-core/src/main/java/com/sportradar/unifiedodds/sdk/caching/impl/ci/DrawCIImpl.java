/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.DrawCI;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawResultCI;
import com.sportradar.unifiedodds.sdk.entities.DrawStatus;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * A draw cache item implementation
 */
public class DrawCIImpl implements DrawCI {
    private static final Logger logger = LoggerFactory.getLogger(DrawCIImpl.class);

    private final URN id;

    private final DataRouterManager dataRouterManager;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    private final Set<Locale> cachedLocales = Sets.newConcurrentHashSet();
    private final ReentrantLock fetchLock = new ReentrantLock();

    private URN lotteryId;
    private Date scheduled;
    private DrawStatus status;
    private List<DrawResultCI> results;
    private Integer displayId;


    DrawCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    DrawCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIDrawFixture data, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        merge(data, dataLocale);
    }

    DrawCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIDrawEvent data, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        merge(data, dataLocale);
    }

    DrawCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPIDrawSummary data, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        merge(data, dataLocale);
    }

    /**
     * Returns the {@link URN} representing id of the related entity
     *
     * @return the {@link URN} representing id of the related entity
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled; otherwise null;
     */
    @Override
    public Date getScheduled() {
        if (scheduled != null || !cachedLocales.isEmpty()) {
            return scheduled;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale));

        return scheduled;
    }

    /**
     * Returns the status of the draw
     *
     * @return the status of the draw
     */
    @Override
    public DrawStatus getStatus() {
        if (status != null || !cachedLocales.isEmpty()) {
            return status;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale));

        return status;
    }

    /**
     * Returns a list of draw results
     *
     * @param locales a {@link List} specifying the required languages
     * @return a list of draw results
     */
    @Override
    public List<DrawResultCI> getResults(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return results == null ? null : ImmutableList.copyOf(results);
        }

        requestMissingSummaryData(locales);

        return results == null ? null : ImmutableList.copyOf(results);
    }

    /**
     * Returns the associated lottery id
     *
     * @return the associated lottery id
     */
    @Override
    public URN getLotteryId() {
        if (lotteryId != null || !cachedLocales.isEmpty()) {
            return lotteryId;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale));

        return lotteryId;
    }

    /**
     * Returns the display id
     *
     * @return the display id
     */
    @Override
    public Integer getDisplayId() {
        if (displayId != null || !cachedLocales.isEmpty()) {
            return displayId;
        }

        requestMissingSummaryData(Collections.singletonList(defaultLocale));

        return displayId;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end; otherwise null;
     */
    @Override
    public Date getScheduledEnd() {
        return null; // no scheduled end
    }

    /**
     * Returns the {@link Map} containing translated names of the item
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the item
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        return Collections.emptyMap(); // no names
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return cachedLocales.containsAll(localeList);
    }

    /**
     * Merges the new provided data into the cache item
     *
     * @param endpointData the data to be merged
     * @param dataLocale the locale in which the data is provided
     * @param <T> the type in which the data is provided
     */
    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPIDrawFixture) {
            internalMerge((SAPIDrawFixture) endpointData, dataLocale);
        } else if (endpointData instanceof SAPIDrawEvent) {
            internalMerge((SAPIDrawEvent) endpointData, dataLocale);
        } else if (endpointData instanceof SAPIDrawSummary) {
            internalMerge((SAPIDrawSummary) endpointData, dataLocale);
        }
    }

    private void internalMerge(SAPIDrawSummary endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        internalMerge(endpointData.getDrawFixture(), dataLocale);

        if (endpointData.getDrawResult() != null && endpointData.getDrawResult().getDraws() != null) {
            mergeResults(endpointData.getDrawResult().getDraws(), dataLocale);
        }

        cachedLocales.add(dataLocale);
    }

    private void internalMerge(SAPIDrawFixture endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        scheduled = endpointData.getDrawDate() == null
                ? null
                : endpointData.getDrawDate().toGregorianCalendar().getTime();

        lotteryId = endpointData.getLottery() == null
                ? null
                : URN.parse(endpointData.getLottery().getId());

        status = map(endpointData.getStatus());

        if(endpointData.getDisplayId() != null)
        {
            displayId = endpointData.getDisplayId();
        }
    }

    private void internalMerge(SAPIDrawEvent endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        scheduled = endpointData.getScheduled() == null
                ? null
                : endpointData.getScheduled().toGregorianCalendar().getTime();

        status = map(endpointData.getStatus());

        if(endpointData.getDisplayId() != null)
        {
            displayId = endpointData.getDisplayId();
        }
    }

    private void mergeResults(SAPIDrawResult.SAPIDraws endpointData, Locale dataLocale) {
        Preconditions.checkNotNull(endpointData);
        Preconditions.checkNotNull(dataLocale);

        List<SAPIDrawResult.SAPIDraws.SAPIDraw> drawResults = endpointData.getDraw();
        if (drawResults != null && !drawResults.isEmpty()) {
            if (results == null) {
                results = drawResults.stream().map(r -> new DrawResultCI(r, dataLocale)).collect(Collectors.toList());
            } else {
                results.forEach(cachedResult ->
                    drawResults.stream()
                            .filter(newResult -> newResult.getValue().equals(cachedResult.getValue()))
                            .findFirst()
                            .ifPresent(newResult -> cachedResult.merge(newResult, dataLocale))
                );
            }
        }
    }

    private void requestMissingSummaryData(List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);

        List<Locale> missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        fetchLock.lock();
        try {
            // recheck missing locales after lock
            missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug("Fetching missing draw data for id='{}' for languages '{}'",
                    id, missingLocales.stream()
                            .map(Locale::getLanguage).collect(Collectors.joining(", ")));

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestDrawSummary(l, id, this);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingSummaryData(%s)", missingLocales), e);
        } finally {
            fetchLock.unlock();
        }
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("DrawCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing DrawCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing DrawCI[{}] request({}), ex:", id, request, e);
            }
        }
    }

    private static DrawStatus map(SAPIDrawStatus status) {
        if (status == null) {
            return DrawStatus.Unknown;
        }

        switch (status) {
            case OPEN:
                return DrawStatus.Open;
            case CLOSED:
                return DrawStatus.Closed;
            case FINISHED:
                return DrawStatus.Finished;
            case CANCELED:
                return DrawStatus.Cancelled;
            default:
                return DrawStatus.Unknown;
        }
    }
}
