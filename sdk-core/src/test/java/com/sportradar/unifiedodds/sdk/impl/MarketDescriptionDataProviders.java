/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.conn.Identifiable;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;

public final class MarketDescriptionDataProviders {

    private MarketDescriptionDataProviders() {}

    public static DataProvider<MarketDescriptions> providingList(
        LanguageHolder language,
        DescMarket marketDescription
    ) throws DataProviderException {
        DataProvider<com.sportradar.uf.sportsapi.datamodel.MarketDescriptions> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(getMarketDescriptions(marketDescription)).when(dataProvider).getData(language.get());
        return dataProvider;
    }

    public static DataProvider<MarketDescriptions> providingList(
        LanguageHolder languageA,
        DescMarket descriptionA,
        LanguageHolder languageB,
        DescMarket descriptionB
    ) throws DataProviderException {
        DataProvider<com.sportradar.uf.sportsapi.datamodel.MarketDescriptions> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(getMarketDescriptions(descriptionA)).when(dataProvider).getData(languageA.get());
        doReturn(getMarketDescriptions(descriptionB)).when(dataProvider).getData(languageB.get());
        return dataProvider;
    }

    public static DataProvider<MarketDescriptions> providing(
        LanguageHolder language,
        Identifiable variant,
        DescMarket market
    ) throws DataProviderException {
        DataProvider<com.sportradar.uf.sportsapi.datamodel.MarketDescriptions> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(getMarketDescriptions(market))
            .when(dataProvider)
            .getData(language.get(), market.getId() + "", variant.id());
        return dataProvider;
    }

    public static DataProvider<MarketDescriptions> providing(
        LanguageHolder language,
        int marketId,
        Identifiable variant,
        MarketDescriptions descriptions
    ) throws DataProviderException {
        DataProvider<com.sportradar.uf.sportsapi.datamodel.MarketDescriptions> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(descriptions).when(dataProvider).getData(language.get(), marketId + "", variant.id());
        return dataProvider;
    }

    public static DataProvider<MarketDescriptions> providing(
        LanguageHolder languageA,
        Identifiable variantA,
        DescMarket marketA,
        LanguageHolder languageB,
        Identifiable variantB,
        DescMarket marketB
    ) throws DataProviderException {
        DataProvider<com.sportradar.uf.sportsapi.datamodel.MarketDescriptions> dataProvider = mock(
            DataProvider.class
        );
        when(dataProvider.getData(languageA.get(), marketA.getId() + "", variantA.id()))
            .thenReturn(getMarketDescriptions(marketA));

        when(dataProvider.getData(languageB.get(), marketB.getId() + "", variantB.id()))
            .thenReturn(getMarketDescriptions(marketB));
        return dataProvider;
    }

    public static DataProvider<MarketDescriptions> providing(
        LanguageHolder languageA,
        String variantIdA,
        DescMarket marketA,
        LanguageHolder languageB,
        String variantIdB,
        DescMarket marketB
    ) throws DataProviderException {
        DataProvider<com.sportradar.uf.sportsapi.datamodel.MarketDescriptions> dataProvider = mock(
            DataProvider.class
        );
        when(dataProvider.getData(languageA.get(), marketA.getId() + "", variantIdA))
            .thenReturn(getMarketDescriptions(marketA));

        when(dataProvider.getData(languageB.get(), marketB.getId() + "", variantIdB))
            .thenReturn(getMarketDescriptions(marketB));
        return dataProvider;
    }

    private static MarketDescriptions getMarketDescriptions(DescMarket marketDescription) {
        MarketDescriptions descriptions = new MarketDescriptions();
        descriptions.getMarket().add(marketDescription);
        return descriptions;
    }
}
