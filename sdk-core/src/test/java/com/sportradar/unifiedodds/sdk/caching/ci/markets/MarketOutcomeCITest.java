package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MarketOutcomeCITest {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Test
    public void constructor_MissingName_ShouldCreateInstance() {
        DescOutcomes.Outcome outcome = new DescOutcomes.Outcome();
        outcome.setId("1");
        MarketOutcomeCI marketOutcome = new MarketOutcomeCI(outcome, DEFAULT_LOCALE);
        assertNotNull(marketOutcome);
        assertEquals("1", marketOutcome.getId());
        assertEquals("", marketOutcome.getName(DEFAULT_LOCALE));
    }
}
