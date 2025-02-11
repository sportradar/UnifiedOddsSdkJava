/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;

public class WaitingUofListenerDi {

    private WaitingUofListenerDi() {}

    public static WaitingUofListener.Factory createWaitingUofListenerFactory() {
        return new WaitingUofListener.Factory(
            SignallingOnPollingQueue.createSignallingOnPollingQueue(new TimeUtilsImpl())
        );
    }
}
