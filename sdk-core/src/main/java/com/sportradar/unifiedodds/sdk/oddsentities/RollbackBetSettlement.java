/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;

/**
 * Rollback is sent when a previously sent bet_settlement was sent in error and needs to be
 * rollbacked
 */
public interface RollbackBetSettlement<T extends SportEvent> extends MarketMessage<T> {
}
