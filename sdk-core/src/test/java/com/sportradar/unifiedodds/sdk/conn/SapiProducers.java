/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;

import com.sportradar.uf.sportsapi.datamodel.Producer;
import com.sportradar.utils.generic.testing.Booleans;
import com.sportradar.utils.generic.testing.RandomInteger;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public final class SapiProducers {

    public static final String LIVE = "live";
    public static final String PREMATCH = "prematch";
    public static final String VIRTUAL = "virtual";

    private SapiProducers() {}

    public static Producer buildActiveProducer(ProducerId id) {
        Producer producer = createAllProducers().get(id);
        producer.setActive(true);
        return producer;
    }

    private static Map<ProducerId, Producer> createAllProducers() {
        try {
            Map<ProducerId, Producer> producers = new HashMap<>();
            producers.put(ProducerId.LIVE_ODDS, createLiveOddsProducer());
            producers.put(ProducerId.BETRADAR_CTRL, createBetradarCtrlProducer());
            producers.put(ProducerId.BETPAL, createBetPalProducer());
            producers.put(ProducerId.PREMIUM_CRICKET, createPremiumCricketProducer());
            producers.put(ProducerId.VIRTUAL_FOOTBALL, createVirtualFootballProducer());
            producers.put(ProducerId.NUMBERS_BETTING, createNumbersBettingProducer());
            producers.put(ProducerId.VIRTUAL_BASKETBALL, createVirtualBasketballProducer());
            producers.put(ProducerId.VIRTUAL_TENNIS_OPEN, createVirtualTennisOpenProducer());
            producers.put(ProducerId.VIRTUAL_DOG_RACING, createVirtualDogRacingProducer());
            producers.put(ProducerId.VIRTUAL_HORSE_RACING, createVirtualHorseRacingProducer());
            producers.put(ProducerId.VIRTUAL_TENNIS_IN_PLAY, createVirtualTennisInPlayProducer());
            producers.put(ProducerId.COMPETITION_ODDS, createCompetitionOddsProducer());
            producers.put(ProducerId.VIRTUAL_BASEBALL, createVirtualBaseballProducer());
            producers.put(ProducerId.PERFORMANCE_BETTING, createPerformanceBettingProducer());
            producers.put(ProducerId.VIRTUAL_CRICKET, createVirtualCricketProducer());
            return producers;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Producer createLiveOddsProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.LIVE_ODDS.get());
        producer.setName("LO");
        producer.setDescription("Live Odds");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope("live");
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createBetradarCtrlProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.BETRADAR_CTRL.get());
        producer.setName("Ctrl");
        producer.setDescription("Betradar Ctrl");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope("prematch");
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createBetPalProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.BETPAL.get());
        producer.setName("BetPal");
        producer.setDescription("BetPal");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(LIVE);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createPremiumCricketProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.PREMIUM_CRICKET.get());
        producer.setName("PremiumCricket");
        producer.setDescription("Premium Cricket");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(LIVE + "|" + PREMATCH);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualFootballProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_FOOTBALL.get());
        producer.setName("VF");
        producer.setDescription("Virtual Football");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createNumbersBettingProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.NUMBERS_BETTING.get());
        producer.setName("WNS");
        producer.setDescription("Numbers Betting");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(PREMATCH);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualBasketballProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_BASKETBALL.get());
        producer.setName("VBL");
        producer.setDescription("Virtual Basketball League");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualTennisOpenProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_TENNIS_OPEN.get());
        producer.setName("VTO");
        producer.setDescription("Virtual Tennis Open");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualDogRacingProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_DOG_RACING.get());
        producer.setName("VDR");
        producer.setDescription("Virtual Dog Racing");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualHorseRacingProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_HORSE_RACING.get());
        producer.setName("VHC");
        producer.setDescription("Virtual Horse Racing");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualTennisInPlayProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_TENNIS_IN_PLAY.get());
        producer.setName("VTI");
        producer.setDescription("Virtual Tennis In Play");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createCompetitionOddsProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.COMPETITION_ODDS.get());
        producer.setName("C-Odds");
        producer.setDescription("Competition Odds");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(LIVE);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualBaseballProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_BASEBALL.get());
        producer.setName("VBI");
        producer.setDescription("Virtual Baseball In-Play");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createPerformanceBettingProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.PERFORMANCE_BETTING.get());
        producer.setName("PB");
        producer.setDescription("Performance Betting");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(LIVE);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static Producer createVirtualCricketProducer() throws MalformedURLException {
        Producer producer = new Producer();
        producer.setId(ProducerId.VIRTUAL_CRICKET.get());
        producer.setName("VCI");
        producer.setDescription("Virtual Cricket In Play");
        producer.setApiUrl(anyHttpUrl().toString());
        producer.setActive(Booleans.any());
        producer.setScope(VIRTUAL);
        producer.setStatefulRecoveryWindowInMinutes(anyRecoveryWindow());
        return producer;
    }

    private static int anyRecoveryWindow() {
        final int minOneHour = 60;
        final int max5Days = 7200;
        return RandomInteger.fromRangeInclusive(minOneHour, max5Days);
    }
}
