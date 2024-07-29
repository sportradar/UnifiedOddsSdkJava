/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import com.sportradar.unifiedodds.sdk.entities.EventClock;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.PeriodScore;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods used to access match specific status attributes
 */
@SuppressWarnings("MultipleStringLiterals")
public interface MatchStatus extends CompetitionStatus {
    /**
     * Returns an {@link EventClock} instance describing the timings in the current event
     *
     * @return an {@link EventClock} instance describing the timings in the current event
     */
    EventClock getEventClock();

    /**
     * Returns a {@link List} of period scores
     *
     * @return a {@link List} of period scores
     */
    List<PeriodScore> getPeriodScores();

    /**
     * Returns the match status id
     *
     * @return the match status id
     */
    int getMatchStatusId();

    /**
     * Returns the match status translated in the default locale
     *
     * @return the match status translated in the default locale
     */
    LocalizedNamedValue getMatchStatus();

    /**
     * Returns the match status translated in the specified language
     *
     * @param locale  a {@link Locale} specifying the language of the status
     * @return the match status translated in the specified language
     */
    LocalizedNamedValue getMatchStatus(Locale locale);

    /**
     * Returns the score of the home competitor competing on the associated sport event
     *
     * @return the score of the home competitor competing on the associated sport event
     */
    BigDecimal getHomeScore();

    /**
     * Returns the score of the away competitor competing on the associated sport event
     *
     * @return the score of the away competitor competing on the associated sport event
     */
    BigDecimal getAwayScore();

    default MatchStatistics getStatistics() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the penalty score of the home competitor competing on the associated sport event (for Ice Hockey)
     * @return value of home penalty score
     */
    default Integer getHomePenaltyScore() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the penalty score of the away competitor competing on the associated sport event (for Ice Hockey)
     * @return value of away penalty score
     */
    default Integer getAwayPenaltyScore() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns an indication if the status is decided by fed
     * @return boolean value
     */
    default Boolean isDecidedByFed() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
