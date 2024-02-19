/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.AMQP;

public class DeclareOkAllOperationsUnsupported implements AMQP.Queue.DeclareOk {

    @Override
    public String getQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMessageCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getConsumerCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int protocolClassId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int protocolMethodId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String protocolMethodName() {
        throw new UnsupportedOperationException();
    }
}
