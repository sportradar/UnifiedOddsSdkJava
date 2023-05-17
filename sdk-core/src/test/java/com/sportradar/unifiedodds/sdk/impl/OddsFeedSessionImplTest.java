/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.MessageInterest.AllMessages;
import static com.sportradar.unifiedodds.sdk.MessageInterest.PrematchMessagesOnly;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.GlobalReplay;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Replay;
import static com.sportradar.unifiedodds.sdk.oddsentities.ProducerStubs.stubLiveProducer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.common.cache.Cache;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CompositeMessageProcessor;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
@SuppressWarnings("ClassFanOutComplexity")
public class OddsFeedSessionImplTest {

    private static final int LIVE_PRODUCER_ID = 1;
    private final RoutingKeyInfo anyRoutingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();
    private final List<String> anyRoutingKeys = Arrays.asList("any");
    private final MessageTimestamp timestamp = mock(MessageTimestamp.class);
    private final SDKInternalConfiguration configuration = mock(SDKInternalConfiguration.class);
    private final MessageReceiver messageReceiver = mock(MessageReceiver.class);
    private final RecoveryManager recoveryManager = mock(RecoveryManager.class);
    private final CompositeMessageProcessor processor = mock(CompositeMessageProcessor.class);
    private final SDKProducerManager producerManager = mock(SDKProducerManager.class);
    private final SportsInfoManager sportsInfoManager = mock(SportsInfoManager.class);
    private final SportEntityFactory sportEntityFactory = mock(SportEntityFactory.class);
    private final FeedMessageFactory messageFactory = mock(FeedMessageFactory.class);
    private final FeedMessageValidator messageValidator = mock(FeedMessageValidator.class);
    private final UnifiedOddsStatistics statistics = mock(UnifiedOddsStatistics.class);
    private final Cache<String, String> dispatchedFixtureChangeCache = mock(Cache.class);
    private OddsFeedListener listener = mock(OddsFeedListener.class);
    private OddsFeedExtListener extListener = mock(OddsFeedExtListener.class);
    private final OddsFeedSessionImpl session = new OddsFeedSessionImpl(
        configuration,
        messageReceiver,
        recoveryManager,
        processor,
        producerManager,
        sportsInfoManager,
        sportEntityFactory,
        messageFactory,
        messageValidator,
        statistics,
        dispatchedFixtureChangeCache
    );

    private Object[] everyEnvironment() {
        return Environment.values();
    }

    private Object[] nonReplayEnvironments() {
        return Arrays
            .asList(Environment.values())
            .stream()
            .filter(e -> e != Replay)
            .filter(e -> e != GlobalReplay)
            .toArray();
    }

    private Object[] replayEnvironments() {
        return new Environment[] { Replay, GlobalReplay };
    }

    @Test
    @Parameters(method = "everyEnvironment")
    public void validMessagesShouldBeProcessedIn(final Environment environment) throws IOException {
        session.open(anyRoutingKeys, AllMessages, listener, extListener);
        val oddsChange = new UFOddsChange();
        when(configuration.getEnvironment()).thenReturn(environment);
        when(producerManager.getProducer(LIVE_PRODUCER_ID)).thenReturn(stubLiveProducer());
        oddsChange.setProduct(LIVE_PRODUCER_ID);
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(true);
        when(messageValidator.validate(any(), any())).thenReturn(ValidationResult.Success);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor).processMessage(any(), any(), any(), any());
    }

    @Test
    public void validMessagesNotOfInterestToProducerShouldBeNotBeProcessed() throws IOException {
        session.open(anyRoutingKeys, PrematchMessagesOnly, listener, extListener);
        val oddsChange = new UFOddsChange();
        when(configuration.getEnvironment()).thenReturn(Environment.Production);
        when(producerManager.getProducer(LIVE_PRODUCER_ID)).thenReturn(stubLiveProducer());
        oddsChange.setProduct(LIVE_PRODUCER_ID);
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(true);
        when(messageValidator.validate(any(), any())).thenReturn(ValidationResult.Success);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor, times(0)).processMessage(any(), any(), any(), any());
    }

    @Test
    @Parameters(method = "nonReplayEnvironments")
    public void messagesOfDisabledProducerShouldNotBeProcessedForNonReplayEnvironments(
        final Environment nonReplay
    ) throws IOException {
        session.open(anyRoutingKeys, AllMessages, listener, extListener);
        when(configuration.getEnvironment()).thenReturn(nonReplay);
        val oddsChange = new UFOddsChange();
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(false);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor, times(0)).processMessage(any(), any(), any(), any());
    }

    @Test
    @Parameters(method = "replayEnvironments")
    public void messagesShouldBeProcessedInReplayEnvironmentsEvenWhenProducerIsDown(final Environment replay)
        throws IOException {
        session.open(anyRoutingKeys, AllMessages, listener, extListener);
        when(configuration.getEnvironment()).thenReturn(replay);
        val oddsChange = new UFOddsChange();
        when(producerManager.getProducer(LIVE_PRODUCER_ID)).thenReturn(stubLiveProducer());
        oddsChange.setProduct(LIVE_PRODUCER_ID);
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(false);
        when(messageValidator.validate(any(), any())).thenReturn(ValidationResult.Success);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor).processMessage(any(), any(), any(), any());
    }
}
