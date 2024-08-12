/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.impl.RoutingKeysTest.TokenName.*;
import static org.junit.Assert.*;

import com.sportradar.utils.Urns;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RoutingKeysTest {

    private static final String DOT = "\\.";

    @Test
    public void priorityOfPreMatchOddsChangeForSomeFootballMatchShouldBeHigh() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        assertEquals("hi", tokens.get(PRIORITY));
    }

    @Test
    public void preMatchOddsChangeForSomeFootballMatchShouldCarryPreMatchInterest() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        assertEquals("pre-match was requested but was not found", "pre", tokens.get(PRE_MATCH_INTEREST));
        assertEquals("non-live was requested but was found something else", "-", tokens.get(LIVE_INTEREST));
    }

    @Test
    public void preMatchOddsChangeForSomeFootballMatchShouldCarryRequestedOddsChangeMessage() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        assertEquals("odds_change", tokens.get(MESSAGE_TYPE));
    }

    @Test
    public void preMatchOddsChangeForSomeFootballMatchShouldCarryFootballSport() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        assertEquals("1", tokens.get(SPORT_ID));
        assertEquals(Urns.Sports.getForFootball(), routingKey.getSportId());
    }

    @Test
    public void preMatchOddsChangeForSomeFootballMatchShouldCarryRequestedSportEvent() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        assertEquals("sr:match", tokens.get(URN_FOR_SPORT_EVENT_ID));
        assertEquals("sr", routingKey.getEventId().getPrefix());
        assertEquals("match", routingKey.getEventId().getType());
    }

    @Test
    public void preMatchOddsChangeForSomeFootballMatchShouldCarryCoherentPositiveSportEventId() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        val eventId = Long.parseLong(tokens.get(SPORT_EVENT_ID_ITSELF));
        assertTrue(eventId > 0);
        assertEquals(eventId, routingKey.getEventId().getId());
    }

    @Test
    public void preMatchOddsChangeForSomeFootballMatchShouldCarryIntegerNodeId() {
        val routingKey = RoutingKeys.getForPreMatchOddsChangeForAnyFootballMatch();

        val tokens = toMap(routingKey.getFullRoutingKey().split(DOT));
        Long.parseLong(tokens.get(NODE_ID));
    }

    private Map<TokenName, String> toMap(final String[] tokens) {
        return IntStream
            .range(0, TokenName.values().length)
            .mapToObj(i -> i)
            .collect(Collectors.toMap(i -> TokenName.values()[i], i -> tokens[i]));
    }

    static enum TokenName {
        PRIORITY,
        PRE_MATCH_INTEREST,
        LIVE_INTEREST,
        MESSAGE_TYPE,
        SPORT_ID,
        URN_FOR_SPORT_EVENT_ID,
        SPORT_EVENT_ID_ITSELF,
        NODE_ID,
    }
}
