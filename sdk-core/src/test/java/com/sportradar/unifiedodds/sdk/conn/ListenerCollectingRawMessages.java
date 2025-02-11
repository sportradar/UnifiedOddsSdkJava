/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.extended.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import java.net.URI;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ListenerCollectingRawMessages implements UofExtListener {

    private final RawMessagesInMemoryStorage messageStorage;

    public static ListenerCollectingRawMessages to(RawMessagesInMemoryStorage messageStorage) {
        return new ListenerCollectingRawMessages(messageStorage);
    }

    @Override
    public void onRawFeedMessageReceived(
        RoutingKeyInfo routingKey,
        UnmarshalledMessage feedMessage,
        MessageTimestamp timestamp,
        MessageInterest messageInterest
    ) {
        messageStorage.append(new ReceivedRawMessage<>(routingKey, feedMessage, timestamp, messageInterest));
    }

    @Override
    public void onRawApiDataReceived(URI uri, Object apiData) {}
}
