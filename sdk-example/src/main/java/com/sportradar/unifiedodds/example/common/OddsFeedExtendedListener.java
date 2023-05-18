/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.URN;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OddsFeedExtendedListener implements OddsFeedExtListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Occurs when any feed message arrives
     *
     * @param routingKey      the routing key associated with this message
     * @param feedMessage     the message received
     * @param timestamp       the message timestamps
     * @param messageInterest the associated {@link MessageInterest}
     */
    @Override
    public void onRawFeedMessageReceived(
        RoutingKeyInfo routingKey,
        UnmarshalledMessage feedMessage,
        MessageTimestamp timestamp,
        MessageInterest messageInterest
    ) {
        logger.info(
            "Received raw feed message [{}]: {} for event {} and timestamp={}",
            messageInterest,
            feedMessage.getClass().getSimpleName(),
            routingKey,
            timestamp.getCreated()
        );
    }

    /**
     * Occurs when data from Sports API arrives
     *
     * @param uri     the uri of the request made
     * @param apiData the data received from the Sports API
     */
    @Override
    public void onRawApiDataReceived(URI uri, Object apiData) {
        logger.info("Dispatching raw api message for {}", uri);
    }
}
