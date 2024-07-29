/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCi;
import com.sportradar.unifiedodds.sdk.entities.EventClock;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.PeriodScore;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.impl.entities.PeriodScoreImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods used to access match status information
 */
@SuppressWarnings({ "ConstantName" })
public class MatchStatusImpl extends CompetitionStatusImpl implements MatchStatus {

    private static final Logger logger = LoggerFactory.getLogger(MatchStatusImpl.class);
    private final SportEventStatusCi statusCi;
    private final LocalizedNamedValueCache matchStatuses;

    public MatchStatusImpl(SportEventStatusCi statusCi, LocalizedNamedValueCache matchStatuses) {
        super(statusCi);
        Preconditions.checkNotNull(statusCi);
        Preconditions.checkNotNull(matchStatuses);

        this.statusCi = statusCi;
        this.matchStatuses = matchStatuses;
    }

    /**
     * Returns an {@link EventClock} instance describing the timings in the current event
     *
     * @return an {@link EventClock} instance describing the timings in the current event
     */
    @Override
    public EventClock getEventClock() {
        return statusCi.getEventClock();
    }

    /**
     * Returns a {@link List} of period scores
     *
     * @return a {@link List} of period scores
     */
    @Override
    public List<PeriodScore> getPeriodScores() {
        return statusCi.getPeriodScores() == null
            ? null
            : statusCi
                .getPeriodScores()
                .stream()
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

    /**
     * Returns the score of the home competitor competing on the associated sport event
     *
     * @return the score of the home competitor competing on the associated sport event
     */
    @Override
    public BigDecimal getHomeScore() {
        return statusCi.getHomeScore();
    }

    /**
     * Returns the score of the away competitor competing on the associated sport event
     *
     * @return the score of the away competitor competing on the associated sport event
     */
    @Override
    public BigDecimal getAwayScore() {
        return statusCi.getAwayScore();
    }

    @Override
    public MatchStatistics getStatistics() {
        return statusCi.getSportEventStatisticsDto() == null
            ? null
            : new MatchStatisticsImpl(statusCi.getSportEventStatisticsDto());
    }

    /**
     * Get the penalty score of the home competitor competing on the associated sport event (for Ice Hockey)
     */
    @Override
    public Integer getHomePenaltyScore() {
        return statusCi.getHomePenaltyScore();
    }

    /**
     * Get the penalty score of the away competitor competing on the associated sport event (for Ice Hockey)
     */
    @Override
    public Integer getAwayPenaltyScore() {
        return statusCi.getAwayPenaltyScore();
    }

    /**
     * Returns an indication if the status is decided by fed
     */
    @Override
    public Boolean isDecidedByFed() {
        return statusCi.isDecidedByFed();
    }
}
