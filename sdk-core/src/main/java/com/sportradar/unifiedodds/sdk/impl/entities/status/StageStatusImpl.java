/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCI;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.status.StageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Provides methods used to access match status information
 */
public class StageStatusImpl extends CompetitionStatusImpl implements StageStatus {
    private final static Logger logger = LoggerFactory.getLogger(StageStatusImpl.class);
    private final SportEventStatusCI statusCI;
    private final LocalizedNamedValueCache matchStatuses;

    public StageStatusImpl(SportEventStatusCI statusCI, LocalizedNamedValueCache matchStatuses) {
        super(statusCI);

        Preconditions.checkNotNull(statusCI);
        Preconditions.checkNotNull(matchStatuses);

        this.statusCI = statusCI;
        this.matchStatuses = matchStatuses;
    }

    /**
     * Returns the match status id
     *
     * @return the match status id
     */
    @Override
    public int getMatchStatusId() {
        return statusCI.getMatchStatusId();
    }

    /**
     * Returns the match status translated in the default locale
     *
     * @return the match status translated in the default locale
     */
    @Override
    public LocalizedNamedValue getMatchStatus() {
        if (statusCI.getMatchStatusId() < 0) {
            logger.warn("Processing match status with id < 0");
            return null;
        }
        return matchStatuses.get(statusCI.getMatchStatusId(), null);
    }

    /**
     * Returns the match status translated in the specified language
     *
     * @param locale a {@link Locale} specifying the language of the status
     * @return the match status translated in the specified language
     */
    @Override
    public LocalizedNamedValue getMatchStatus(Locale locale) {
        if (statusCI.getMatchStatusId() < 0) {
            logger.warn("Processing match status with id < 0");
            return null;
        }
        return matchStatuses.get(statusCI.getMatchStatusId(), Lists.newArrayList(locale));
    }
}
