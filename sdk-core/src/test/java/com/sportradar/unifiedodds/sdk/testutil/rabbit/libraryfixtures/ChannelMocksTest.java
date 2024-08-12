/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ChannelMocksTest {

    @Test
    public void shouldCreateChannelAbleToDeclareQueueWithSpecifiedName() throws IOException {
        val queueName = "specifiedName";

        val channel = ChannelMocks.createDeclaringQueue(queueName);

        assertEquals(queueName, channel.queueDeclare().getQueue());
    }

    @Test
    public void createdChannelShouldBeFurtherMockable() throws IOException {
        val channel = ChannelMocks.createDeclaringQueue("any");

        final int specifiedNumber = 5;
        when(channel.getChannelNumber()).thenReturn(specifiedNumber);

        assertEquals(specifiedNumber, channel.getChannelNumber());
    }
}
