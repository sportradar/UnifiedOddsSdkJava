/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import lombok.val;

public class ChannelMocks {

    private ChannelMocks() {}

    public static Channel createDeclaringQueue(String queueName) throws IOException {
        val declareOk = mock(AMQP.Queue.DeclareOk.class);
        when(declareOk.getQueue()).thenReturn(queueName);
        val channel = mock(Channel.class);
        when(channel.queueDeclare()).thenReturn(declareOk);
        return channel;
    }
}
