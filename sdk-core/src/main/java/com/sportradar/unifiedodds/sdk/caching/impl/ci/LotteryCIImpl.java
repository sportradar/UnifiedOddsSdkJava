/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sportradar.uf.sportsapi.datamodel.SAPILottery;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.LotteryCI;
import com.sportradar.unifiedodds.sdk.caching.ci.BonusInfoCI;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawInfoCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableLotteryCI;
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
 * Created on 18/01/2018.
 * // TODO @eti: Javadoc
 */
public class LotteryCIImpl implements LotteryCI, ExportableCacheItem {
    private static final Logger logger = LoggerFactory.getLogger(LotteryCIImpl.class);

    private final URN id;

    private final DataRouterManager dataRouterManager;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    private final Map<Locale, String> names = Maps.newConcurrentMap();
    private final Set<Locale> cachedLocales = Sets.newConcurrentHashSet();
    private final ReentrantLock fetchLock = new ReentrantLock();

    private URN categoryId;
    private BonusInfoCI bonusInfo;
    private DrawInfoCI drawInfo;
    private List<URN> scheduledDraws;

    LotteryCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    LotteryCIImpl(URN id, DataRouterManager dataRouterManager, Locale defaultLocale, ExceptionHandlingStrategy exceptionHandlingStrategy, SAPILottery data, Locale dataLocale) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);

        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        merge(data, dataLocale);
    }

    LotteryCIImpl(ExportableLotteryCI exportable, DataRouterManager dataRouterManager, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(exportable);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;

        this.defaultLocale = exportable.getDefaultLocale();
        this.id = URN.parse(exportable.getId());
        this.names.putAll(exportable.getNames());
        this.cachedLocales.addAll(exportable.getCachedLocales());
        this.categoryId = exportable.getCategoryId() != null ? URN.parse(exportable.getCategoryId()) : null;
        this.bonusInfo = exportable.getBonusInfo() != null ? new BonusInfoCI(exportable.getBonusInfo()) : null;
        this.drawInfo = exportable.getDrawInfo() != null ? new DrawInfoCI(exportable.getDrawInfo()) : null;
        this.scheduledDraws = exportable.getScheduledDraws() != null ? exportable.getScheduledDraws().stream().map(URN::parse).collect(Collectors.toList()) : null;
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
     * Returns the {@link Map} containing translated names of the item
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the item
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (names.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        requestMissingLotteryData(locales);

        return ImmutableMap.copyOf(names);
    }

    /**
     * Returns the associated category id
     *
     * @return the associated category id
     */
    @Override
    public URN getCategoryId() {
        if (categoryId != null || !cachedLocales.isEmpty()) {
            return categoryId;
        }

        requestMissingLotteryData(Collections.singletonList(defaultLocale));

        return categoryId;
    }

    /**
     * Returns the associated bonus info
     *
     * @return the associated bonus info
     */
    @Override
    public BonusInfoCI getBonusInfo() {
        if (bonusInfo != null || !cachedLocales.isEmpty()) {
            return bonusInfo;
        }

        requestMissingLotteryData(Collections.singletonList(defaultLocale));

        return bonusInfo;
    }

    /**
     * Returns the associated draw info
     *
     * @return the associated draw info
     */
    @Override
    public DrawInfoCI getDrawInfo() {
        if (drawInfo != null || !cachedLocales.isEmpty()) {
            return drawInfo;
        }

        requestMissingLotteryData(Collections.singletonList(defaultLocale));

        return drawInfo;
    }

    /**
     * Returns the lottery draws schedule
     *
     * @return the lottery draw schedule
     */
    @Override
    public List<URN> getScheduledDraws() {
        if (scheduledDraws != null || !cachedLocales.isEmpty()) {
            return scheduledDraws == null ? null : ImmutableList.copyOf(scheduledDraws);
        }

        requestMissingLotteryData(Collections.singletonList(defaultLocale));

        return scheduledDraws == null ? null : ImmutableList.copyOf(scheduledDraws);
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
        return null; // no scheduled info available
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
        return null; // no scheduled end info available
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled (no api request is invoked)
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled; otherwise null;
     */
    @Override
    public Date getScheduledRaw() { return null; }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end (no api request is invoked)
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end; otherwise null;
     */
    @Override
    public Date getScheduledEndRaw() { return null; }
    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        return null;
    }

    /**
     * Returns the {@link URN} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link URN} specifying the replacement sport event for the current instance
     */
    @Override
    public URN getReplacedBy() {
        return null;
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
        if (!(endpointData instanceof SAPILottery)) {
            return;
        }

        SAPILottery lottery = (SAPILottery) endpointData;

        if (lottery.getCategory() != null) {
            categoryId = URN.parse(lottery.getCategory().getId());
        }

        if (lottery.getBonusInfo() != null) {
            bonusInfo = new BonusInfoCI(lottery.getBonusInfo());
        }

        if (lottery.getDrawInfo() != null) {
            drawInfo = new DrawInfoCI(lottery.getDrawInfo());
        }

        if (lottery.getName() != null) {
            names.put(dataLocale, lottery.getName());
        }
        else{
            names.put(dataLocale, "");
        }
    }

    private void requestMissingLotteryData(List<Locale> requiredLocales) {
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

            logger.debug("Fetching missing lottery data for id='{}' for languages '{}'",
                    id, missingLocales.stream()
                            .map(Locale::getLanguage).collect(Collectors.joining(", ")));

            missingLocales.forEach(l -> {
                try {
                    scheduledDraws = dataRouterManager.requestLotterySchedule(l, id, this);
                    cachedLocales.add(l);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingLotteryData(%s)", missingLocales), e);
        } finally {
            fetchLock.unlock();
        }
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("LotteryCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing LotteryCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing LotteryCI[{}] request({}), ex:", id, request, e);
            }
        }
    }

    @Override
    public ExportableCI export() {
        return new ExportableLotteryCI(
                id.toString(),
                new HashMap<>(names),
                null,
                null,
                null,
                null,
                defaultLocale,
                categoryId != null ? categoryId.toString() : null,
                bonusInfo != null ? bonusInfo.export() : null,
                drawInfo != null ? drawInfo.export() : null,
                scheduledDraws != null ? scheduledDraws.stream().map(URN::toString).collect(Collectors.toList()) : null,
                new HashSet<>(cachedLocales)
        );
    }
}
