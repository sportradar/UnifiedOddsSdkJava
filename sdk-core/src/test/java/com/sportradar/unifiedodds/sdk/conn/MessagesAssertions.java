/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.datamodel.UfAlive;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessagesAssertions {

    private RawMessagesQuerier querier;

    public void assertThatSystemAlivesHaveNotDuplicates() {
        List<ReceivedRawMessage<UfAlive>> systemAlives = querier.findAlivesOf(
            MessageInterest.SystemAliveMessages
        );

        assertThatAllHasDifferentTimestamps(systemAlives);
    }

    private static void assertThatAllHasDifferentTimestamps(List<ReceivedRawMessage<UfAlive>> systemAlives) {
        final int amountOfMessages = systemAlives.size();
        if (systemAlives.size() >= 2) {
            long uniqueDeliveries = systemAlives
                .stream()
                .map(m1 -> m1.getFeedMessage().getTimestamp())
                .distinct()
                .count();
            assertThat(uniqueDeliveries).isEqualTo(amountOfMessages);
        } else {
            throw new RuntimeException("not enough message to decide whether there are duplicates");
        }
    }
}
