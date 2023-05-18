/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import java.util.Date;

/**
 * This message is sent to rollback/undo a previously sent out BetCancel message if possible. This
 * could for example happen if our operator during a game by mistake cancels the wrong market
 * (resulting in a cancel bet getting sent). Then realizing the mistake.
 */
public interface RollbackBetCancel<T extends SportEvent> extends MarketMessage<T> {
    /**
     * @return the timestamp from when bets placed should be rejected (if 0 this means all bets).
     *         This should correspond to a startime in a previous betcancel
     */
    Date getStartTime();

    /**
     * @return the end of the period for which bets placed should be rejected (if 0 this means all
     *         bets). This should correspond to the endtime in a previous betcancel
     */
    Date getEndTime();
}
