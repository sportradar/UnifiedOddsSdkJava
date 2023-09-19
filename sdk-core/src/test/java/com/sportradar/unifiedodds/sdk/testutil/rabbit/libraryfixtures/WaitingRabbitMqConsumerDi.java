/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaitingRabbitMqConsumerDi {

    private WaitingRabbitMqConsumerDi() {}

    public static WaitingRabbitMqConsumer.Factory createWaitingRabbitMqConsumerFactory() {
        return new WaitingRabbitMqConsumer.Factory(
            SignallingOnPollingQueue.createSignallingOnPollingQueue(new TimeUtilsImpl())
        );
    }
}
