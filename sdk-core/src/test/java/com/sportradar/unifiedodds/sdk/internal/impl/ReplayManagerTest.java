/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import org.junit.Test;

public class ReplayManagerTest {

    private final double anySpeedUpFactor = 2;
    private final int anyMaxDelayInMilliSeconds = 1000;
    private final Integer anyProducerId = 1;
    private final Boolean shouldRewriteTimestamps = true;
    private final Boolean shouldRunParallel = true;
    private final String anyApiHostAndPort = "host:8181";
    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
    private final SportDataProvider sportDataProvider = mock(SportDataProvider.class);
    private final HttpHelper httpHelper = mock(HttpHelper.class);
    private final Deserializer deserializer = mock(Deserializer.class);
    private final LogHttpDataFetcher logHttpDataFetcher = mock(LogHttpDataFetcher.class);
    private final SportEntityFactory sportEntityFactory = mock(SportEntityFactory.class);

    private final ReplayManager replayManager = new ReplayManager(
        config,
        sportDataProvider,
        httpHelper,
        deserializer,
        logHttpDataFetcher,
        sportEntityFactory
    );

    @Test
    public void replayManagerShouldSendPlayRequestWithParametersAsQueryParameters() throws Exception {
        when(config.getApiHostAndPort()).thenReturn(anyApiHostAndPort);

        HttpHelper.ResponseData anyResponseData = mock(HttpHelper.ResponseData.class);
        when(httpHelper.post(anyString())).thenReturn(anyResponseData);
        when(anyResponseData.isSuccessful()).thenReturn(true);

        replayManager.play(
            anySpeedUpFactor,
            anyMaxDelayInMilliSeconds,
            anyProducerId,
            shouldRewriteTimestamps,
            shouldRunParallel
        );
        String expectedQueryParameters =
            expectedQueryParametersString() + "&run_parallel=" + shouldRunParallel;
        String expectedReplayRequestPath =
            "https://" + anyApiHostAndPort + "/v1/replay/play" + expectedQueryParameters;
        verify(httpHelper, times(1)).post(expectedReplayRequestPath);
    }

    @Test
    public void replayManagerShouldSendScenarioPlayRequestWithParametersAsQueryParameters() throws Exception {
        when(config.getApiHostAndPort()).thenReturn(anyApiHostAndPort);

        HttpHelper.ResponseData anyResponseData = mock(HttpHelper.ResponseData.class);
        when(httpHelper.post(anyString())).thenReturn(anyResponseData);
        when(anyResponseData.isSuccessful()).thenReturn(true);

        int anyScenarioId = 1;
        replayManager.playScenario(
            anyScenarioId,
            anySpeedUpFactor,
            anyMaxDelayInMilliSeconds,
            anyProducerId,
            shouldRewriteTimestamps
        );
        String expectedQueryParameters = expectedQueryParametersString();
        String expectedReplayRequestPath =
            "https://" +
            anyApiHostAndPort +
            "/v1/replay/scenario/play/" +
            anyScenarioId +
            "" +
            expectedQueryParameters;
        verify(httpHelper, times(1)).post(expectedReplayRequestPath);
    }

    private String expectedQueryParametersString() {
        return String.format(
            "?speed=%d&max_delay=%d&node_id=0&product=%d&use_replay_timestamp=%b",
            (int) anySpeedUpFactor,
            anyMaxDelayInMilliSeconds,
            anyProducerId,
            shouldRewriteTimestamps
        );
    }
}
