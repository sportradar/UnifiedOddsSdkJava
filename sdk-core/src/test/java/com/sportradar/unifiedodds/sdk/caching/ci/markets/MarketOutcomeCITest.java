package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import java.util.Locale;
import org.junit.Test;

@SuppressWarnings({ "AbbreviationAsWordInName" })
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
