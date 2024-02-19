/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReceivedRawMessage<T extends UnmarshalledMessage> {

    private final RoutingKeyInfo routingKeyInfo;
    private final T feedMessage;
    private final MessageTimestamp timestamp;
    private final MessageInterest messageInterest;

    public <U extends T> ReceivedRawMessage<U> withMessageAs(Class<U> messageClass) {
        return new ReceivedRawMessage<>(routingKeyInfo, (U) feedMessage, timestamp, messageInterest);
    }
}
