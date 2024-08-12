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
import com.sportradar.uf.datamodel.UfOddsChange;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CompositeMessageProcessor;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
public class UofSessionImplTest {

    private static final int LIVE_PRODUCER_ID = 1;
    private final RoutingKeyInfo anyRoutingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();
    private final List<String> anyRoutingKeys = Arrays.asList("any");
    private final MessageTimestamp timestamp = mock(MessageTimestamp.class);
    private final SdkInternalConfiguration configuration = mock(SdkInternalConfiguration.class);
    private final MessageReceiver messageReceiver = mock(MessageReceiver.class);
    private final RecoveryManager recoveryManager = mock(RecoveryManager.class);
    private final CompositeMessageProcessor processor = mock(CompositeMessageProcessor.class);
    private final SdkProducerManager producerManager = mock(SdkProducerManager.class);
    private final SportDataProvider sportDataProvider = mock(SportDataProvider.class);
    private final SportEntityFactory sportEntityFactory = mock(SportEntityFactory.class);
    private final FeedMessageFactory messageFactory = mock(FeedMessageFactory.class);
    private final FeedMessageValidator messageValidator = mock(FeedMessageValidator.class);
    private final UnifiedOddsStatistics statistics = mock(UnifiedOddsStatistics.class);
    private final Cache<String, String> dispatchedFixtureChangeCache = mock(Cache.class);
    private UofListener listener = mock(UofListener.class);
    private UofExtListener extListener = mock(UofExtListener.class);
    private final UofSessionImpl session = new UofSessionImpl(
        configuration,
        messageReceiver,
        recoveryManager,
        processor,
        producerManager,
        sportDataProvider,
        sportEntityFactory,
        messageFactory,
        messageValidator,
        statistics,
        dispatchedFixtureChangeCache
    );

    private static Object[] everyEnvironment() {
        return Environment.values();
    }

    private static Object[] nonReplayEnvironments() {
        return Arrays
            .asList(Environment.values())
            .stream()
            .filter(e -> e != Replay)
            .filter(e -> e != GlobalReplay)
            .toArray();
    }

    private static Object[] replayEnvironments() {
        return new Environment[] { Replay, GlobalReplay };
    }

    @ParameterizedTest
    @MethodSource("everyEnvironment")
    public void validMessagesShouldBeProcessedIn(final Environment environment) throws IOException {
        session.open(anyRoutingKeys, AllMessages, listener, extListener);
        val oddsChange = new UfOddsChange();
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
        val oddsChange = new UfOddsChange();
        when(configuration.getEnvironment()).thenReturn(Environment.Production);
        when(producerManager.getProducer(LIVE_PRODUCER_ID)).thenReturn(stubLiveProducer());
        oddsChange.setProduct(LIVE_PRODUCER_ID);
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(true);
        when(messageValidator.validate(any(), any())).thenReturn(ValidationResult.Success);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor, times(0)).processMessage(any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("nonReplayEnvironments")
    public void messagesOfDisabledProducerShouldNotBeProcessedForNonReplayEnvironments(
        final Environment nonReplay
    ) throws IOException {
        session.open(anyRoutingKeys, AllMessages, listener, extListener);
        when(configuration.getEnvironment()).thenReturn(nonReplay);
        val oddsChange = new UfOddsChange();
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(false);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor, times(0)).processMessage(any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("replayEnvironments")
    public void messagesShouldBeProcessedInReplayEnvironmentsEvenWhenProducerIsDown(final Environment replay)
        throws IOException {
        session.open(anyRoutingKeys, AllMessages, listener, extListener);
        when(configuration.getEnvironment()).thenReturn(replay);
        val oddsChange = new UfOddsChange();
        when(producerManager.getProducer(LIVE_PRODUCER_ID)).thenReturn(stubLiveProducer());
        oddsChange.setProduct(LIVE_PRODUCER_ID);
        when(producerManager.isProducerEnabled(LIVE_PRODUCER_ID)).thenReturn(false);
        when(messageValidator.validate(any(), any())).thenReturn(ValidationResult.Success);

        session.onMessageReceived(oddsChange, new byte[0], anyRoutingKey, timestamp);

        verify(processor).processMessage(any(), any(), any(), any());
    }
}
