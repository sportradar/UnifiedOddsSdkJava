/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.OddEvenMarket.EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.OddEvenMarket.ODD_OUTCOME_ID;
import static java.util.Arrays.stream;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class SapiMarketDescriptions {

    private SapiMarketDescriptions() {}

    public static DescMarket oddEvenDescription() {
        return oddEvenDescription(Locale.ENGLISH);
    }

    public static DescMarket oddEvenDescription(Locale language) {
        return oddEvenDescription(MarketTranslation.getFor(language));
    }

    public static DescMarket oddEvenDescription(MarketTranslation translation) {
        DescMarket market = new DescMarket();
        market.setId(OddEvenMarket.ID);
        market.setName(translation.marketName);
        market.setOutcomes(new DescOutcomes());
        market.getOutcomes().getOutcome().add(oddOutcomeDescription());
        market.getOutcomes().getOutcome().add(evenOutcomeDescription());
        return market;
    }

    public static DescOutcomes.Outcome oddOutcomeDescription() {
        return oddOutcomeDescription(Locale.ENGLISH);
    }

    public static DescOutcomes.Outcome oddOutcomeDescription(Locale language) {
        return oddOutcomeDescription(MarketTranslation.getFor(language));
    }

    public static DescOutcomes.Outcome oddOutcomeDescription(MarketTranslation translation) {
        DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
        evenOutcome.setId(ODD_OUTCOME_ID);
        evenOutcome.setName(translation.getOddOutcomeName());
        return evenOutcome;
    }

    public static DescOutcomes.Outcome evenOutcomeDescription() {
        return evenOutcomeDescription(Locale.ENGLISH);
    }

    public static DescOutcomes.Outcome evenOutcomeDescription(Locale language) {
        return evenOutcomeDescription(MarketTranslation.getFor(language));
    }

    public static DescOutcomes.Outcome evenOutcomeDescription(MarketTranslation translation) {
        DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
        evenOutcome.setId(EVEN_OUTCOME_ID);
        evenOutcome.setName(translation.getEvenOutcomeName());
        return evenOutcome;
    }

    @RequiredArgsConstructor
    @Getter
    private static enum MarketTranslation {
        EN(Locale.ENGLISH, "Odd/Even", "Odd", "Even"),
        FR(Locale.FRENCH, "Pair/Impair", "impair", "pair");

        private final Locale language;
        private final String marketName;
        private final String oddOutcomeName;
        private final String evenOutcomeName;

        public static MarketTranslation getFor(Locale language) {
            return stream(MarketTranslation.values())
                .filter(translation -> translation.language.equals(language))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Language not supported by test fixture"));
        }
    }
}
