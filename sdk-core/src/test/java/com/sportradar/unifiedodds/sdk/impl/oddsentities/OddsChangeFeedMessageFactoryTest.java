/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.datamodel.UfOddsChange;
import com.sportradar.uf.datamodel.UfOddsChange.UfOdds;
import com.sportradar.uf.datamodel.UfOddsChangeMarket;
import com.sportradar.uf.datamodel.UfOddsGenerationProperties;
import com.sportradar.unifiedodds.sdk.caching.fixtures.NamedValueStub;
import com.sportradar.unifiedodds.sdk.caching.fixtures.NamedValuesProviderFixture;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;

public class OddsChangeFeedMessageFactoryTest {

    private final UfOddsChange message = new UfOddsChange();
    private final MarketFactory marketFactory = mock(MarketFactory.class);
    private final NamedValuesProviderFixture namedValuesProviderFixture = new NamedValuesProviderFixture();
    private final SdkProducerManager producerManager = mock(SdkProducerManager.class);
    private final byte[] rawMessage = new byte[0];
    private final MessageTimestamp timestamp = mock(MessageTimestamp.class);
    private final SportEvent sportEvent = mock(SportEvent.class);
    private final FeedMessageFactoryImpl factory = new FeedMessageFactoryImpl(
        marketFactory,
        namedValuesProviderFixture,
        producerManager
    );

    @Test
    public void shouldNotConstructWithoutSportEvent() {
        assertThatThrownBy(() -> factory.buildOddsChange(null, message, rawMessage, timestamp))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sportEvent");
    }

    @Test
    public void shouldNotConstructWithoutOddsChangeMessage() {
        assertThatThrownBy(() -> factory.buildOddsChange(sportEvent, null, rawMessage, timestamp))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("message");
    }

    @Test
    public void shouldNotConstructWithoutRawMessage() {
        assertThatThrownBy(() -> factory.buildOddsChange(sportEvent, message, null, timestamp))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("rawMessage");
    }

    @Test
    public void shouldNotConstructFactoryWithoutMarketFactory() {
        assertThatThrownBy(() -> new FeedMessageFactoryImpl(null, namedValuesProviderFixture, producerManager)
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("marketFactory");
    }

    @Test
    public void shouldNotConstructWithoutMarketFactory() {
        assertThatThrownBy(() ->
                new OddsChangeImpl<>(
                    sportEvent,
                    message,
                    null,
                    rawMessage,
                    null,
                    namedValuesProviderFixture,
                    timestamp
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("marketFactory");
    }

    @Test
    public void shouldNotConstructFactoryWithoutNamedValueProvider() {
        assertThatThrownBy(() -> new FeedMessageFactoryImpl(marketFactory, null, producerManager))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("namedValuesProvider");
    }

    @Test
    public void shouldNotConstructWithoutNamedValueProvider() {
        assertThatThrownBy(() ->
                new OddsChangeImpl<>(sportEvent, message, null, rawMessage, marketFactory, null, timestamp)
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("namedValuesProvider");
    }

    @Test
    public void shouldNotConstructFactoryWithoutProducerManager() {
        assertThatThrownBy(() -> new FeedMessageFactoryImpl(marketFactory, namedValuesProviderFixture, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("producerManager");
    }

    @Test
    public void shouldNotConstructWithoutTimestamp() {
        assertThatThrownBy(() -> factory.buildOddsChange(sportEvent, message, rawMessage, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("timestamp");
    }

    @Test
    public void shouldConstructWithExpectedTotals() {
        final double expectedTotals = 0.4d;
        val props = new UfOddsGenerationProperties();
        props.setExpectedTotals(expectedTotals);
        message.setOddsGenerationProperties(props);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals((Double) expectedTotals, oddsChange.getOddsGenerationProperties().getExpectedTotals());
    }

    @Test
    public void shouldConstructWithExpectedSupremacy() {
        final double expectedSupremacy = 0.5d;
        val props = new UfOddsGenerationProperties();
        props.setExpectedSupremacy(expectedSupremacy);
        message.setOddsGenerationProperties(props);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(
            (Double) expectedSupremacy,
            oddsChange.getOddsGenerationProperties().getExpectedSupremacy()
        );
    }

    @Test
    public void shouldBuildNoMarketsIfMassageCarriesNoOdds() {
        message.setOdds(null);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(Collections.emptyList(), oddsChange.getMarkets());
    }

    @Test
    public void shouldBuildNoMarketsIfMassageCarriesNullMarkets() {
        val odds = mock(UfOdds.class);
        when(odds.getMarket()).thenReturn(null);
        message.setOdds(odds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(Collections.emptyList(), oddsChange.getMarkets());
    }

    @Test
    public void shouldBuildNoMarketsIfMessageCarriesZeroMarkets() {
        val odds = mock(UfOdds.class);
        when(odds.getMarket()).thenReturn(Collections.emptyList());
        message.setOdds(odds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(Collections.emptyList(), oddsChange.getMarkets());
    }

    @Test
    public void shouldBuildNoMarketsIfMarketCarriedByMessageWasNotBuildable() {
        val odds = mock(UfOdds.class);
        val market = mock(UfOddsChangeMarket.class);
        when(odds.getMarket()).thenReturn(Arrays.asList(market));
        when(marketFactory.buildMarketWithOdds(any(), eq(market), anyInt())).thenReturn(Optional.empty());
        message.setOdds(odds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(Collections.emptyList(), oddsChange.getMarkets());
    }

    @Test
    public void shouldBuildOneMarketsIfMessageCarriesOneMarket() {
        val market = mock(MarketWithOdds.class);
        val xmlOdds = mock(UfOdds.class);
        val xmlMarket = mock(UfOddsChangeMarket.class);
        when(xmlOdds.getMarket()).thenReturn(Arrays.asList(xmlMarket));
        when(marketFactory.buildMarketWithOdds(any(), eq(xmlMarket), anyInt()))
            .thenReturn(Optional.of(market));
        message.setOdds(xmlOdds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(Arrays.asList(market), oddsChange.getMarkets());
    }

    @Test
    public void shouldPreserveBetstopReason() {
        final int betstopReasonId = 3;
        val betstopReasonDescription = "human_readable_betstop_reason";
        val betstopReason = new NamedValueStub(betstopReasonId, betstopReasonDescription);
        namedValuesProviderFixture.stubBetstopReason(betstopReason);
        val xmlOdds = mock(UfOdds.class);
        when(xmlOdds.getBetstopReason()).thenReturn(betstopReasonId);
        message.setOdds(xmlOdds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(betstopReasonDescription, oddsChange.getBetstopReason());
        assertEquals(betstopReason, oddsChange.getBetstopReasonValue());
    }

    @Test
    public void shouldNotProvideBetstopReasonIfMessageDoesNotContainBetstopReason() {
        val xmlOdds = mock(UfOdds.class);
        when(xmlOdds.getBetstopReason()).thenReturn(null);
        message.setOdds(xmlOdds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertNull(oddsChange.getBetstopReason());
        assertNull(oddsChange.getBetstopReasonValue());
    }

    @Test
    public void shouldPreserveBettingStatus() {
        final int bettingStatusId = 3;
        val description = "human_readable_betting_statuses_description";
        val bettingStatus = new NamedValueStub(bettingStatusId, description);
        namedValuesProviderFixture.stubBettingStatus(bettingStatus);
        val xmlOdds = mock(UfOdds.class);
        when(xmlOdds.getBettingStatus()).thenReturn(bettingStatusId);
        message.setOdds(xmlOdds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertEquals(description, oddsChange.getBettingStatus());
        assertEquals(bettingStatus, oddsChange.getBettingStatusValue());
    }

    @Test
    public void shouldNotProvideBettingStatusDescriptionIfMessageDoesNotContainBettingStatus() {
        val xmlOdds = mock(UfOdds.class);
        when(xmlOdds.getBettingStatus()).thenReturn(null);
        message.setOdds(xmlOdds);

        val oddsChange = factory.buildOddsChange(sportEvent, message, rawMessage, timestamp);

        assertNull(oddsChange.getBettingStatus());
        assertNull(oddsChange.getBettingStatusValue());
    }
}
