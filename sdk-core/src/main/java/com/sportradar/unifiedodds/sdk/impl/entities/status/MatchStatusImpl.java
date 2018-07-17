/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.entities.EventClock;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.PeriodScore;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.unifiedodds.sdk.impl.entities.PeriodScoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Provides methods used to access match status information
 */
public class MatchStatusImpl extends CompetitionStatusImpl implements MatchStatus {
    private final static Logger logger = LoggerFactory.getLogger(MatchStatusImpl.class);
    private final SportEventStatusDTO statusDto;
    private final LocalizedNamedValueCache matchStatuses;


    public MatchStatusImpl(SportEventStatusDTO statusDto, LocalizedNamedValueCache matchStatuses) {
        super(statusDto);

        Preconditions.checkNotNull(statusDto);
        Preconditions.checkNotNull(matchStatuses);

        this.statusDto = statusDto;
        this.matchStatuses = matchStatuses;
    }


    /**
     * Returns an {@link EventClock} instance describing the timings in the current event
     *
     * @return an {@link EventClock} instance describing the timings in the current event
     */
    @Override
    public EventClock getEventClock() {
        return statusDto.getEventClock();
    }

    /**
     * Returns a {@link List} of period scores
     *
     * @return a {@link List} of period scores
     */
    @Override
    public List<PeriodScore> getPeriodScores() {
        return statusDto.getPeriodScores() == null ? null :
                ImmutableList.copyOf(statusDto.getPeriodScores().stream()
                        .map(ps -> new PeriodScoreImpl(ps, matchStatuses))
                        .collect(Collectors.toList()));
    }

    /**
     * Returns the match status id
     *
     * @return the match status id
     */
    @Override
    public int getMatchStatusId() {
        return statusDto.getMatchStatusId();
    }

    /**
     * Returns the match status translated in the default locale
     *
     * @return the match status translated in the default locale
     */
    @Override
    public LocalizedNamedValue getMatchStatus() {
        if (statusDto.getMatchStatusId() < 0) {
            logger.warn("Processing match status with id < 0");
            return null;
        }
        return matchStatuses.get(statusDto.getMatchStatusId(), null);
    }

    /**
     * Returns the match status translated in the specified language
     *
     * @param locale a {@link Locale} specifying the language of the status
     * @return the match status translated in the specified language
     */
    @Override
    public LocalizedNamedValue getMatchStatus(Locale locale) {
        if (statusDto.getMatchStatusId() < 0) {
            logger.warn("Processing match status with id < 0");
            return null;
        }
        return matchStatuses.get(statusDto.getMatchStatusId(), Lists.newArrayList(locale));
    }

    /**
     * Returns the score of the home competitor competing on the associated sport event
     *
     * @return the score of the home competitor competing on the associated sport event
     */
    @Override
    public BigDecimal getHomeScore() {
        return statusDto.getHomeScore();
    }

    /**
     * Returns the score of the away competitor competing on the associated sport event
     *
     * @return the score of the away competitor competing on the associated sport event
     */
    @Override
    public BigDecimal getAwayScore() {
        return statusDto.getAwayScore();
    }
}
