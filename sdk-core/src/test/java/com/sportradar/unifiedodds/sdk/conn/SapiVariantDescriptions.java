/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.DescVariant;
import com.sportradar.uf.sportsapi.datamodel.DescVariantOutcomes;
import com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds;
import java.util.Locale;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class SapiVariantDescriptions {

    private static void expectSameOutcomeIds(TranslatedOutcomes marketTranslation, MarketVariant variant) {
        if (marketTranslation.getOutcomeTranslations().size() != variant.outcomeIds().size()) {
            throw new IllegalArgumentException("The number of outcome ids and translations must be the same");
        }
        if (!marketTranslation.getOutcomeTranslations().keySet().containsAll(variant.outcomeIds())) {
            throw new IllegalArgumentException("The outcome ids must be the same");
        }
    }

    public static class ExactGoals {

        private static final Map<String, String> TRANSLATIONS = ImmutableMap
            .<String, String>builder()
            .put("sr:exact_goals:5+:1336", "0")
            .put("sr:exact_goals:5+:1337", "1")
            .put("sr:exact_goals:5+:1338", "2")
            .put("sr:exact_goals:5+:1339", "3")
            .put("sr:exact_goals:5+:1340", "4")
            .put("sr:exact_goals:5+:1341", "5+")
            .build();

        public static DescVariant fivePlusVariantDescription(Locale language) {
            return fivePlusVariantDescription(MarketTranslation.getFor(language));
        }

        public static DescVariant fivePlusVariantDescription() {
            return fivePlusVariantDescription(MarketTranslation.EN);
        }

        private static DescVariant fivePlusVariantDescription(MarketTranslation translation) {
            DescVariant variant = new DescVariant();
            variant.setId("sr:exact_goals:5+");
            variant.setOutcomes(populateOutcomesValidating(translation));
            return variant;
        }

        private static DescVariantOutcomes populateOutcomesValidating(MarketTranslation translation) {
            expectSameOutcomeIds(translation, ExactGoalsMarketIds.fivePlusVariant());
            return populateOutcomes(translation);
        }

        private static DescVariantOutcomes populateOutcomes(MarketTranslation translation) {
            DescVariantOutcomes outcomes = new DescVariantOutcomes();
            ExactGoalsMarketIds
                .fivePlusVariant()
                .outcomeIds()
                .forEach(id ->
                    outcomes.getOutcome().add(outcome(id, translation.getOutcomeTranslations().get(id)))
                );
            return outcomes;
        }

        private static DescVariantOutcomes.Outcome outcome(String id, String name) {
            DescVariantOutcomes.Outcome outcome = new DescVariantOutcomes.Outcome();
            outcome.setId(id);
            outcome.setName(name);
            return outcome;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation implements TranslatedOutcomes {
            EN(Locale.ENGLISH, TRANSLATIONS),
            FR(Locale.FRENCH, TRANSLATIONS);

            private final Locale language;
            private final Map<String, String> outcomeTranslations;

            public static ExactGoals.MarketTranslation getFor(Locale language) {
                for (MarketTranslation translation : values()) {
                    if (translation.language.equals(language)) {
                        return translation;
                    }
                }
                throw new IllegalArgumentException("Language not supported: " + language);
            }
        }
    }

    interface TranslatedOutcomes {
        Map<String, String> getOutcomeTranslations();
    }
}
