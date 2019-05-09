/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCI;
import com.sportradar.unifiedodds.sdk.entities.EventClock;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.PeriodScore;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.impl.entities.PeriodScoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Provides methods used to access match status information
 */
public class MatchStatusImpl extends CompetitionStatusImpl implements MatchStatus {
    private final static Logger logger = LoggerFactory.getLogger(MatchStatusImpl.class);
    private final SportEventStatusCI statusCI;
    private final LocalizedNamedValueCache matchStatuses;

    public MatchStatusImpl(SportEventStatusCI statusCI, LocalizedNamedValueCache matchStatuses) {
        super(statusCI);

        Preconditions.checkNotNull(statusCI);
        Preconditions.checkNotNull(matchStatuses);

        this.statusCI = statusCI;
        this.matchStatuses = matchStatuses;
    }

    /**
     * Returns an {@link EventClock} instance describing the timings in the current event
     *
     * @return an {@link EventClock} instance describing the timings in the current event
     */
    @Override
    public EventClock getEventClock() {
        return statusCI.getEventClock();
    }

    /**
     * Returns a {@link List} of period scores
     *
     * @return a {@link List} of period scores
     */
    @Override
    public List<PeriodScore> getPeriodScores() {
        return statusCI.getPeriodScores() == null ? null :
                statusCI.getPeriodScores().stream()
                        .map(ps -> new PeriodScoreImpl(ps, matchStatuses))
                        .collect(ImmutableList.toImmutableList());
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

    /**
     * Returns the score of the home competitor competing on the associated sport event
     *
     * @return the score of the home competitor competing on the associated sport event
     */
    @Override
    public BigDecimal getHomeScore() {
        return statusCI.getHomeScore();
    }

    /**
     * Returns the score of the away competitor competing on the associated sport event
     *
     * @return the score of the away competitor competing on the associated sport event
     */
    @Override
    public BigDecimal getAwayScore() {
        return statusCI.getAwayScore();
    }

    /**
     * Get the penalty score of the home competitor competing on the associated sport event (for Ice Hockey)
     */
    @Override
    public Integer getHomePenaltyScore() {
        return statusCI.getHomePenaltyScore();
    }

    /**
     * Get the penalty score of the away competitor competing on the associated sport event (for Ice Hockey)
     */
    @Override
    public Integer getAwayPenaltyScore() { return statusCI.getAwayPenaltyScore(); }
}
