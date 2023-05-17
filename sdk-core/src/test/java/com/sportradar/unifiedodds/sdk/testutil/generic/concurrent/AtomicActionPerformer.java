/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

public class AtomicActionPerformer {

    public synchronized void perform(Runnable runnable) {
        runnable.run();
    }
}
