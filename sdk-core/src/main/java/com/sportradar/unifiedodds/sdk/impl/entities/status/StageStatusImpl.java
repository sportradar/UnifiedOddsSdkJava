/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCi;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.status.StageStatus;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods used to access match status information
 */
@SuppressWarnings({ "ConstantName" })
public class StageStatusImpl extends CompetitionStatusImpl implements StageStatus {

    private static final Logger logger = LoggerFactory.getLogger(StageStatusImpl.class);
    private final SportEventStatusCi statusCi;
    private final LocalizedNamedValueCache matchStatuses;

    public StageStatusImpl(SportEventStatusCi statusCi, LocalizedNamedValueCache matchStatuses) {
        super(statusCi);
        Preconditions.checkNotNull(statusCi);
        Preconditions.checkNotNull(matchStatuses);

        this.statusCi = statusCi;
        this.matchStatuses = matchStatuses;
    }

    /**
     * Returns the match status id
     *
     * @return the match status id
     */
    @Override
    public int getMatchStatusId() {
        return statusCi.getMatchStatusId();
    }

    /**
     * Returns the match status translated in the default locale
     *
     * @return the match status translated in the default locale
     */
    @Override
    public LocalizedNamedValue getMatchStatus() {
        if (statusCi.getMatchStatusId() < 0) {
            logger.warn("Processing match status with id < 0");
            return null;
        }
        return matchStatuses.get(statusCi.getMatchStatusId(), null);
    }

    /**
     * Returns the match status translated in the specified language
     *
     * @param locale a {@link Locale} specifying the language of the status
     * @return the match status translated in the specified language
     */
    @Override
    public LocalizedNamedValue getMatchStatus(Locale locale) {
        if (statusCi.getMatchStatusId() < 0) {
            logger.warn("Processing match status with id < 0");
            return null;
        }
        return matchStatuses.get(statusCi.getMatchStatusId(), Lists.newArrayList(locale));
    }
}
