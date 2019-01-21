/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;

/**
 * Defines methods implemented by classes capable of processing feed messages
 */
public interface FeedMessageProcessor {
    /**
     * Returns the processor identifier
     *
     * @return - the processor identifier
     */
    String getProcessorId();

    /**
     * Processes the provided message. If the <i>next message processor</i> is defined
     * trough the {@link #setNextMessageProcessor(FeedMessageProcessor)}, the instance should forward the
     * message to the defined <i>next message processor</i> after the processing is completed
     *
     * @param message - the message that should be processed
     * @param body - the raw body of the received message
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     * @param timestamp - all message timestamps
     */
    void processMessage(UnmarshalledMessage message, byte[] body, RoutingKeyInfo routingKeyInfo, MessageTimestamp timestamp);

    /**
     * Sets the next message processor that should be invoked after the message processing is finished
     *
     * @param nextMessageProcessor - the {@link FeedMessageProcessor} implementation that should be
     *                               invoked after the message process is finished
     */
    void setNextMessageProcessor(FeedMessageProcessor nextMessageProcessor);
}
