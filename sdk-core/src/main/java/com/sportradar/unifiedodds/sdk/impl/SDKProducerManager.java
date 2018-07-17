/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.ProducerManager;

/**
 * Created on 03/07/2017.
 * // TODO @eti: Javadoc
 */
public interface SDKProducerManager extends ProducerManager {
    void open();

    void setProducerDown(int producerId, boolean flaggedDown);

    void internalSetProducerLastMessageTimestamp(int producerId, long lastMessageTimestamp);

    void setLastProcessedMessageGenTimestamp(int producerId, long lastProcessedMessageGenTimestamp);

    void setLastAliveReceivedGenTimestamp(int producerId, long aliveReceivedGenTimestamp);
}
