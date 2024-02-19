/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.domain.language.Translations;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.testutil.generic.collections.Maps;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.val;
import lombok.var;

public final class MarketDescriptions {

    private MarketDescriptions() {}

    public static MarketDescription namesOf(DescMarket sapiMarketDesc, LanguageHolder language) {
        return MarketDescriptions.NameFocused.singleMarketFrom(Maps.of(language.get(), sapiMarketDesc));
    }

    public static MarketDescription namesOf(
        DescMarket sapiMarketDesc1,
        LanguageHolder language1,
        DescMarket sapiMarketDesc2,
        LanguageHolder language2
    ) {
        return MarketDescriptions.NameFocused.singleMarketFrom(
            Maps.of(language1.get(), sapiMarketDesc1, language2.get(), sapiMarketDesc2)
        );
    }

    static class NameFocused {

        public static MarketDescriptionStub singleMarketFrom(Map<Locale, DescMarket> translatedMarket) {
            int marketId = getMarketId(translatedMarket);
            Translations marketName = Translations.importFrom(onlyMarketNameOf(translatedMarket));
            List<OutcomeDescription> outcomes = extractOutcomeDescriptions(translatedMarket);
            return new MarketDescriptionStub().withId(marketId).with(marketName).with(outcomes);
        }

        private static List<OutcomeDescription> extractOutcomeDescriptions(
            Map<Locale, DescMarket> translatedMarket
        ) {
            Map<String, Map<Locale, String>> translatedOutcomesByOutcomeId = new HashMap<>();
            setUpOutcomeIds(translatedMarket, translatedOutcomesByOutcomeId);
            groupOutcomeTranslationsByOutcomeId(translatedMarket, translatedOutcomesByOutcomeId);
            val translatedOutcomes = toOutcomeDescriptions(translatedOutcomesByOutcomeId);
            return translatedOutcomes;
        }

        private static List<OutcomeDescription> toOutcomeDescriptions(
            Map<String, Map<Locale, String>> translatedOutcomesByOutcomeId
        ) {
            List<OutcomeDescription> translatedOutcomes = new ArrayList<>();
            for (var singleLanguageOutcomeTranslation : translatedOutcomesByOutcomeId.entrySet()) {
                String outcomeId = singleLanguageOutcomeTranslation.getKey();
                translatedOutcomes.add(
                    new NameFocusedOutcomeDescriptionStub(
                        outcomeId,
                        Translations.importFrom(singleLanguageOutcomeTranslation.getValue())
                    )
                );
            }
            return translatedOutcomes;
        }

        private static int getMarketId(Map<Locale, DescMarket> translatedMarket) {
            val marketIds = translatedMarket
                .values()
                .stream()
                .map(DescMarket::getId)
                .distinct()
                .collect(Collectors.toList());
            if (marketIds.size() > 1) {
                throw new IllegalStateException("Test fixture is not designed to hold more than one market");
            }
            return marketIds.get(0);
        }

        private static void groupOutcomeTranslationsByOutcomeId(
            Map<Locale, DescMarket> translatedMarket,
            Map<String, Map<Locale, String>> translatedOutcomes
        ) {
            for (Map.Entry<Locale, DescMarket> singleLanguageMarketTranslation : translatedMarket.entrySet()) {
                val language = singleLanguageMarketTranslation.getKey();
                val market = singleLanguageMarketTranslation.getValue();
                market
                    .getOutcomes()
                    .getOutcome()
                    .forEach(outcome -> {
                        translatedOutcomes.get(outcome.getId()).put(language, outcome.getName());
                    });
            }
        }

        private static void setUpOutcomeIds(
            Map<Locale, DescMarket> translatedMarket,
            Map<String, Map<Locale, String>> translatedOutcomes
        ) {
            translatedMarket
                .values()
                .stream()
                .forEach(market ->
                    market
                        .getOutcomes()
                        .getOutcome()
                        .forEach(outcome -> translatedOutcomes.put(outcome.getId(), new HashMap<>()))
                );
        }

        private static Map<Locale, String> onlyMarketNameOf(Map<Locale, DescMarket> markets) {
            return markets
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().getName()));
        }

        private static Function<DescOutcomes.Outcome, NameFocusedOutcomeDescriptionStub> toOutcomeStub(
            Locale language
        ) {
            return o -> {
                val outcomeTranslation = new Translations(language, o.getName());
                return new NameFocusedOutcomeDescriptionStub(o.getId(), outcomeTranslation);
            };
        }
    }
}
