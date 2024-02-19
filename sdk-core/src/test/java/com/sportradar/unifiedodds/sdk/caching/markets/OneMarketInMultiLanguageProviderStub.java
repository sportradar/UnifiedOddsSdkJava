/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OneMarketInMultiLanguageProviderStub implements MarketDescriptionProvider {

    private final List<Locale> availableLanguages;
    private final MarketDescription marketDescription;
    private final List<Locale> availableLanguagesAfterReloading;
    private final MarketDescription marketDescriptionAfterReloading;

    private int reloadMarketDescriptionsCalledTimes;

    @Override
    public MarketDescription getMarketDescription(
        int marketId,
        Map<String, String> marketSpecifiers,
        List<Locale> requestedLanguages,
        boolean fetchVariantDescriptions
    ) throws CacheItemNotFoundException {
        if (getAvailableLanguages().containsAll(requestedLanguages)) {
            return getMarketDescription();
        } else {
            throw new CacheItemNotFoundException(
                "market description provider test fixture " +
                "does not contain translations for all requested languages"
            );
        }
    }

    private MarketDescription getMarketDescription() {
        if (reloadMarketDescriptionsCalledTimes == 0) {
            return marketDescription;
        } else {
            return marketDescriptionAfterReloading;
        }
    }

    private List<Locale> getAvailableLanguages() {
        if (reloadMarketDescriptionsCalledTimes == 0) {
            return availableLanguages;
        } else {
            return availableLanguagesAfterReloading;
        }
    }

    @Override
    public boolean reloadMarketDescription(int marketId, Map<String, String> marketSpecifiers) {
        reloadMarketDescriptionsCalledTimes++;
        return true;
    }

    public void verifyDescriptionWasReloaded() {
        assertThat(reloadMarketDescriptionsCalledTimes).isGreaterThan(0);
    }

    public void verifyDescriptionWasReloadedTimes(int times) {
        assertThat(reloadMarketDescriptionsCalledTimes).isEqualTo(times);
    }

    @AllArgsConstructor
    public static class Builder {

        private final List<Locale> availableLanguages;

        private final MarketDescription marketDescription;

        private List<Locale> availableLanguagesAfterReloading;

        private MarketDescription marketDescriptionAfterReloading;

        public static Builder providing(
            List<Locale> availableLanguages,
            MarketDescription marketDescription
        ) {
            return new Builder(availableLanguages, marketDescription, availableLanguages, marketDescription);
        }

        public Builder andAfterReloading(List<Locale> language, MarketDescription description) {
            availableLanguagesAfterReloading = language;
            marketDescriptionAfterReloading = description;
            return this;
        }

        public OneMarketInMultiLanguageProviderStub build() {
            return new OneMarketInMultiLanguageProviderStub(
                availableLanguages,
                marketDescription,
                availableLanguagesAfterReloading,
                marketDescriptionAfterReloading
            );
        }
    }
}
