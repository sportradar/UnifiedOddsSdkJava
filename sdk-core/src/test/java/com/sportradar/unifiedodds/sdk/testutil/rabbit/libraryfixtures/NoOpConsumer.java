/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import com.rabbitmq.client.*;
import java.io.IOException;

public class NoOpConsumer implements Consumer {

    @Override
    public void handleConsumeOk(String consumerTag) {}

    @Override
    public void handleCancelOk(String consumerTag) {}

    @Override
    public void handleCancel(String consumerTag) throws IOException {}

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {}

    @Override
    public void handleRecoverOk(String consumerTag) {}

    @Override
    public void handleDelivery(
        String consumerTag,
        Envelope envelope,
        AMQP.BasicProperties properties,
        byte[] body
    ) throws IOException {}
}
