/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.extended;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;

import java.net.URI;

/**
 * Interface to handle received messages and data from Sports API.
 *
 */
public interface OddsFeedExtListener {

    /**
     * Occurs when any feed message arrives
     *
     * @param routingKey the routing key associated with this message
     * @param feedMessage the message received
     * @param timestamp the message timestamps
     * @param messageInterest the associated {@link MessageInterest}
     */
    void onRawFeedMessageReceived(RoutingKeyInfo routingKey, UnmarshalledMessage feedMessage, MessageTimestamp timestamp, MessageInterest messageInterest);

    /**
     * Occurs when data from Sports API arrives
     *
     * @param uri the uri of the request made
     * @param apiData the data received from the Sports API
     */
    void onRawApiDataReceived(URI uri, Object apiData);
}
