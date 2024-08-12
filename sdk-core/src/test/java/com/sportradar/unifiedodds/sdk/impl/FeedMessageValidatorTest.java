/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.ValidationResult.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Injector;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.di.TestInjectorFactory;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import java.util.Locale;
import java.util.function.Consumer;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "ClassFanOutComplexity", "MultipleStringLiterals", "VisibilityModifier" })
public class FeedMessageValidatorTest {

    private static final String INVALID_EVENT_ID = "some_event_id";
    private static final String INVALID_SPECIFIERS = "some_specifiers";

    //    SdkInternalConfiguration config = Mockito.mock(SdkInternalConfiguration.class);

    FeedMessageValidator validator;
    RoutingKeyParser keyParser;

    @BeforeEach
    public void setup() {
        val config = configurationWithAnyDefaultLanguageAndThrowingOnErrors();
        Injector injector = new TestInjectorFactory(config, new StubUofConfiguration()).create();
        keyParser = injector.getInstance(RoutingKeyParser.class);

        validator = injector.getInstance(FeedMessageValidator.class);
    }

    //Odds Change:

    @Test
    public void validOddsChange() {
        testOddsChange(oddsChange -> {}, Success);
    }

    @Test
    public void invalidEventIdOddsChange() {
        testOddsChange(oddsChange -> oddsChange.setEventId(INVALID_EVENT_ID), Success);
    }

    @Test
    public void nullOddsOddsChange() {
        testOddsChange(oddsChange -> oddsChange.setOddsChangeReason(null), Success);
    }

    @Test
    public void emptyMarketsOddsChange() {
        testOddsChange(Success, "test/feed_xml/odds_change_no_markets.xml");
    }

    @Test
    public void emptyOutcomesOddsChange() {
        testOddsChange(Success, "test/feed_xml/odds_change_no_outcomes.xml");
    }

    @Test
    public void invalidSpecifiersOddsChange() {
        testMsg(
            (UfOddsChange oddsChange) ->
                oddsChange.getOdds().getMarket().get(0).setSpecifiers(INVALID_SPECIFIERS),
            ProblemsDetected,
            ODDS_CHANGE_KEY,
            "test/feed_xml/odds_change_single_market.xml"
        );
    }

    private void testOddsChange(Consumer<UfOddsChange> updater, ValidationResult expectedResult) {
        testMsg(updater, expectedResult, ODDS_CHANGE_KEY, ODDS_CHANGE_MSG_URI);
    }

    private void testOddsChange(ValidationResult expectedResult, String msgUri) {
        testMsg(msg -> {}, expectedResult, ODDS_CHANGE_KEY, msgUri);
    }

    //Bet Stop:

    @Test
    public void validBetStop() {
        testBetStop(betStop -> {}, Success);
    }

    @Test
    public void emptyGroupsBetStop() {
        testBetStop(betStop -> betStop.setGroups(""), Failure);
    }

    private void testBetStop(Consumer<UfBetStop> updater, ValidationResult expectedResult) {
        testMsg(updater, expectedResult, BET_STOP_KEY, BET_STOP_MSG_URI);
    }

    //Bet Settlement:

    @Test
    public void validBetSettlement() {
        testBetSettlement(betSettlement -> {}, Success);
    }

    @Test
    public void invalidEventIdBetSettlement() {
        testBetSettlement(betSettlement -> betSettlement.setEventId(INVALID_EVENT_ID), Success);
    }

    @Test
    public void emptyMarketsBetSettlement() {
        UfBetSettlement.UfOutcomes emptyMarkets = new UfBetSettlement.UfOutcomes();

        testBetSettlement(betSettlement -> betSettlement.setOutcomes(emptyMarkets), Success);
    }

    @Test
    public void nullMarketsBetSettlement() {
        testBetSettlement(betSettlement -> betSettlement.setOutcomes(null), Success);
    }

    @Test
    public void nullSpecifiersBetSettlement() {
        testBetSettlement(
            betSettlement -> betSettlement.getOutcomes().getMarket().get(0).setSpecifiers(null),
            Success
        );
    }

    @Test
    public void invalidSpecifiersBetSettlement() {
        testBetSettlement(
            betSettlement -> betSettlement.getOutcomes().getMarket().get(0).setSpecifiers(INVALID_SPECIFIERS),
            ProblemsDetected
        );
    }

    private void testBetSettlement(Consumer<UfBetSettlement> updater, ValidationResult expectedResult) {
        testMsg(updater, expectedResult, BET_SETTLEMENT_KEY, BET_SETTLEMENT_MSG_URI);
    }

    //Bet Cancel:

    @Test
    public void validBetCancel() {
        testBetCancel(betCancel -> {}, Success);
    }

    @Test
    public void invalidEventIdBetCancel() {
        testBetCancel(betCancel -> betCancel.setEventId(INVALID_EVENT_ID), Success);
    }

    @Test
    public void emptyMarketsBetCancel() {
        testMsg(betCancel -> {}, Success, BET_CANCEL_KEY, "test/feed_xml/bet_cancel_no_markets.xml");
    }

    @Test
    public void nullSpecifiersBetCancel() {
        testBetCancel(betCancel -> betCancel.getMarket().get(0).setSpecifiers(null), Success);
    }

    @Test
    public void invalidSpecifiersBetCancel() {
        testBetCancel(
            betCancel -> betCancel.getMarket().get(0).setSpecifiers(INVALID_SPECIFIERS),
            ProblemsDetected
        );
    }

    private void testBetCancel(Consumer<UfBetCancel> updater, ValidationResult expectedResult) {
        testMsg(updater, expectedResult, BET_CANCEL_KEY, BET_CANCEL_MSG_URI);
    }

    //Snapshot Complete:

    @Test
    public void validSnapshotCompleted() {
        testMsg(snapshotComplete -> {}, Success, SNAPSHOT_COMPLETE_KEY, SNAPSHOT_COMPLETE_MSG_URI);
    }

    @Test
    public void zeroRequestIdSnapshotCompleted() {
        testMsg(
            (UfSnapshotComplete snapshotComplete) -> snapshotComplete.setRequestId(0L),
            Success,
            SNAPSHOT_COMPLETE_KEY,
            SNAPSHOT_COMPLETE_MSG_URI
        );
    }

    //Alive:

    @Test
    public void validAlive() {
        testAlive(alive -> {}, Success);
    }

    @Test
    public void negativeSubscriberAlive() {
        testAlive(alive -> alive.setSubscribed(-1), ProblemsDetected);
    }

    private void testAlive(Consumer<UfAlive> updater, ValidationResult expectedResult) {
        testMsg(updater, expectedResult, ALIVE_KEY, ALIVE_MSG_URI);
    }

    //Fixture Change:

    @Test
    public void validFixtureChange() {
        testFixtureChange(fixtureChange -> {}, Success);
    }

    @Test
    public void invalidEventIdFixtureChange() {
        testFixtureChange(
            (UfFixtureChange fixtureChange) -> fixtureChange.setEventId(INVALID_EVENT_ID),
            Success
        );
    }

    private void testFixtureChange(Consumer<UfFixtureChange> updater, ValidationResult expectedResult) {
        testMsg(
            updater,
            expectedResult,
            "hi.pre.live.fixture_change.1.sr:match.8816128.1",
            FIXTURE_CHANGE_MSG_URI
        );
    }

    //Rollback Bet Settlement:

    @Test
    public void validRollbackBetSettlement() {
        testRollbackBetSettlement(msg -> {}, Success);
    }

    @Test
    public void invalidEventIdRollbackBetSettlement() {
        testRollbackBetSettlement(msg -> msg.setEventId(INVALID_EVENT_ID), Success);
    }

    @Test
    public void emptyMarketsRollbackBetSettlement() {
        testRollbackBetSettlement(Success, "test/feed_xml/rollback_bet_settlement_no_markets.xml");
    }

    @Test
    public void nullSpecifiersRollbackBetSettlement() {
        testRollbackBetSettlement(Success, "test/feed_xml/rollback_bet_settlement_no_specifiers.xml");
    }

    @Test
    public void invalidSpecifiersRollbackBetSettlement() {
        testRollbackBetSettlement(
            msg -> msg.getMarket().get(0).setSpecifiers(INVALID_SPECIFIERS),
            ProblemsDetected
        );
    }

    private void testRollbackBetSettlement(
        Consumer<UfRollbackBetSettlement> updater,
        ValidationResult expectedResult
    ) {
        testMsg(
            updater,
            expectedResult,
            "hi.-.live.rollback_bet_settlement.1.sr:match.10237855.-",
            ROLLBACK_BET_SETTLEMENT_MSG_URI
        );
    }

    private void testRollbackBetSettlement(ValidationResult expectedResult, String msgUri) {
        testMsg(msg -> {}, expectedResult, "hi.-.live.rollback_bet_settlement.1.sr:match.10237855.-", msgUri);
    }

    //Rollback Bet Cancel:

    @Test
    public void validRollbackBetCancel() {
        testRollbackBetCancel(msg -> {}, Success);
    }

    @Test
    public void invalidEventIdRollbackBetCancel() {
        testRollbackBetCancel(msg -> msg.setEventId(INVALID_EVENT_ID), Success);
    }

    @Test
    public void emptyMarketsRollbackBetCancel() {
        testMsg(
            msg -> {},
            Success,
            "hi.-.live.rollback_bet_settlement.1.sr:match.10237855.-",
            "test/feed_xml/rollback_bet_cancel_no_markets.xml"
        );
    }

    private void testRollbackBetCancel(
        Consumer<UfRollbackBetCancel> updater,
        ValidationResult expectedResult
    ) {
        testMsg(
            updater,
            expectedResult,
            "hi.-.live.rollback_bet_settlement.1.sr:match.10237855.-",
            ROLLBACK_BET_CANCEL_MSG_URI
        );
    }

    //Helpers:

    private <T extends UnmarshalledMessage> void testMsg(
        Consumer<T> updater,
        ValidationResult expectedResult,
        String routingKey,
        String msgUri
    ) {
        RoutingKeyInfo routingKeyInfo = keyParser.getRoutingKeyInfo(routingKey);

        try {
            T msg = XmlMessageReader.readMessageFromResource(msgUri);

            updater.accept(msg);

            assertEquals(expectedResult, validator.validate(msg, routingKeyInfo));
        } catch (DeserializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static SdkInternalConfiguration configurationWithAnyDefaultLanguageAndThrowingOnErrors() {
        SdkInternalConfiguration mock = mock(SdkInternalConfiguration.class);
        when(mock.getDefaultLocale()).thenReturn(Locale.UK);
        when(mock.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        return mock;
    }
}
