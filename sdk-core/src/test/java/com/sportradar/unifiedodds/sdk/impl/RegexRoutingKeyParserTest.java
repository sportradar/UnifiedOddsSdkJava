package com.sportradar.unifiedodds.sdk.impl;

import static org.junit.Assert.assertEquals;

import com.sportradar.utils.Urn;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "VisibilityModifier" })
public class RegexRoutingKeyParserTest {

    RoutingKeyParser parser = new RegexRoutingKeyParser();

    @Test
    public void betSettlementKeyIsParsedCorrectly() {
        String key = "lo.-.live.bet_settlement.5.sr:match.9583179";

        RoutingKeyInfo sportId = parser.getRoutingKeyInfo(key);

        assertEquals(sportId.getSportId(), Urn.parse("sr:sport:5"));
    }

    @Test
    public void oddsChangeKeyIsParsedCorrectly() {
        String key = "hi.-.live.odds_change.6.sr:match.9536715";

        RoutingKeyInfo sportId = parser.getRoutingKeyInfo(key);

        assertEquals(sportId.getSportId(), Urn.parse("sr:sport:6"));
    }

    @Test
    public void doesNotThrowWhenRoutingKeyInvalid() {
        //wrong message type name: expected: odds_change, actual: oddschange
        String key = "hi.-.live.oddschange.6.sr:match.9536715";

        parser.getRoutingKeyInfo(key);
        Assert.assertNotNull(parser);
    }

    @Test
    public void doesNotThrowWhenRoutingKeyInvalid2() {
        //missing dot before st:match
        String key = "hi.-.live.odds_change.6sr:match.9536715";

        parser.getRoutingKeyInfo(key);
        Assert.assertNotNull(parser);
    }

    @Test
    public void doesNotThrowWhenRoutingKeyInvalid3() {
        //wrong sport id(6b) - it should be a long
        String key = "hi.-.live.odds_change.6b.sr:match.9536715";

        parser.getRoutingKeyInfo(key);
        Assert.assertNotNull(parser);
    }
}
