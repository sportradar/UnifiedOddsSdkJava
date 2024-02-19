/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class MarketDescriptorProviders {

    private MarketDescriptorProviders() {}

    public static MarketDescriptionProvider noMarketDescribingProvider() {
        return new NoMarketDescribingProvider();
    }

    public static OneMarketInMultiLanguageProviderStub providing(
        LanguageHolder language,
        MarketDescription marketDescription
    ) {
        return OneMarketInMultiLanguageProviderStub.Builder
            .providing(singletonList(language.get()), marketDescription)
            .build();
    }

    public static OneMarketInMultiLanguageProviderStub providing(
        LanguageHolder language,
        MarketDescription marketDescription,
        TranslatedMarketDescriptionHolder afterReloading
    ) {
        return OneMarketInMultiLanguageProviderStub.Builder
            .providing(singletonList(language.get()), marketDescription)
            .andAfterReloading(afterReloading.languages, afterReloading.marketDescription)
            .build();
    }

    public static OneMarketInMultiLanguageProviderStub providing(
        LanguageHolder lang1,
        LanguageHolder lang2,
        MarketDescription marketDescription
    ) {
        return OneMarketInMultiLanguageProviderStub.Builder
            .providing(asList(lang1.get(), lang2.get()), marketDescription)
            .build();
    }

    public static OneMarketInMultiLanguageProviderStub providing(
        LanguageHolder lang1,
        LanguageHolder lang2,
        MarketDescription marketDescription,
        TranslatedMarketDescriptionHolder afterReloading
    ) {
        return OneMarketInMultiLanguageProviderStub.Builder
            .providing(asList(lang1.get(), lang2.get()), marketDescription)
            .andAfterReloading(afterReloading.languages, afterReloading.marketDescription)
            .build();
    }

    public static TranslatedMarketDescriptionHolder andAfterReloading(
        LanguageHolder language,
        MarketDescription marketDescription
    ) {
        return new TranslatedMarketDescriptionHolder(singletonList(language.get()), marketDescription);
    }

    public static TranslatedMarketDescriptionHolder andAfterReloading(
        LanguageHolder langA,
        LanguageHolder langB,
        MarketDescription marketDescription
    ) {
        return new TranslatedMarketDescriptionHolder(asList(langA.get(), langB.get()), marketDescription);
    }

    @RequiredArgsConstructor
    public static class TranslatedMarketDescriptionHolder {

        private final List<Locale> languages;
        private final MarketDescription marketDescription;
    }
}
