/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;

public class WaitingRabbitMqConsumerDi {

    private WaitingRabbitMqConsumerDi() {}

    public static WaitingRabbitMqConsumer.Factory createWaitingRabbitMqConsumerFactory() {
        return new WaitingRabbitMqConsumer.Factory(
            SignallingOnPollingQueue.createSignallingOnPollingQueue(new TimeUtilsImpl())
        );
    }
}
