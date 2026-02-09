/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.SneakyThrows;

@SuppressWarnings({ "MagicNumber" })
public class TwoRequestDelayingBarrier extends ResponseDefinitionTransformer {

    private final AtomicInteger requestCounter = new AtomicInteger(0);
    private final CyclicBarrier barrier = new CyclicBarrier(2);

    @Override
    public String getName() {
        return "two-request-delaying-barrier";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    @SneakyThrows
    @Override
    public ResponseDefinition transform(
        Request request,
        ResponseDefinition responseDefinition,
        FileSource files,
        com.github.tomakehurst.wiremock.extension.Parameters parameters
    ) {
        waitUntilBothRequestsHaveArrived();
        releaseRequestsAfterConfiguredDelays(parameters);

        return responseDefinition;
    }

    @SneakyThrows
    private void releaseRequestsAfterConfiguredDelays(Parameters parameters) {
        int firstDelay = parameters.getInt("firstDelayMs");
        int secondDelay = parameters.getInt("secondDelayMs");

        int requestIndex = requestCounter.incrementAndGet();
        int requestDelay = (requestIndex == 1) ? firstDelay : secondDelay;

        Thread.sleep(requestDelay);
    }

    private void waitUntilBothRequestsHaveArrived()
        throws InterruptedException, BrokenBarrierException, TimeoutException {
        barrier.await(10, TimeUnit.SECONDS);
    }
}
