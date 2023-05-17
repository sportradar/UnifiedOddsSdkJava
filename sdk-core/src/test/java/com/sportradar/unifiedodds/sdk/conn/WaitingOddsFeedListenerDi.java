/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;

public class WaitingOddsFeedListenerDi {

    private WaitingOddsFeedListenerDi() {}

    public static WaitingOddsFeedListener.Factory createWaitingOddsFeedListenerFactory() {
        return new WaitingOddsFeedListener.Factory(
            SignallingOnPollingQueue.createSignallingOnPollingQueue(new TimeUtilsImpl())
        );
    }
}
