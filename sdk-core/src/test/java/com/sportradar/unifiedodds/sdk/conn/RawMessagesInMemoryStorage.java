/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.datamodel.UfAlive;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class RawMessagesInMemoryStorage implements RawMessagesQuerier {

    private final Queue<ReceivedRawMessage<UnmarshalledMessage>> feedMessages = new LinkedBlockingDeque<>();

    public void append(ReceivedRawMessage<UnmarshalledMessage> feedMessage) {
        Helper.writeToOutput("Received: " + feedMessage.getFeedMessage());
        feedMessages.add(feedMessage);
    }

    @Override
    public List<ReceivedRawMessage<UfAlive>> findAlivesOf(MessageInterest interest) {
        return feedMessages
            .stream()
            .filter(m -> Objects.equals(m.getMessageInterest(), interest))
            .filter(m -> m.getFeedMessage() instanceof UfAlive)
            .map(m -> m.withMessageAs(UfAlive.class))
            .collect(Collectors.toList());
    }
}
