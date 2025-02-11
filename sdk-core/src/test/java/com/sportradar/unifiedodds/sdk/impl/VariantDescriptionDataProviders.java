/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.DescVariant;
import com.sportradar.uf.sportsapi.datamodel.VariantDescriptions;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.val;

public final class VariantDescriptionDataProviders {

    public static DataProvider<VariantDescriptions> providingList(
        LanguageHolder language,
        DescVariant variantDescription
    ) throws DataProviderException {
        DataProvider<VariantDescriptions> dataProvider = mock(DataProvider.class);
        when(dataProvider.getData(language.get())).thenReturn(getVariantDescriptions(variantDescription));
        return dataProvider;
    }

    public static DataProvider<VariantDescriptions> providingList(
        LanguageHolder languageA,
        DescVariant variantDescriptionA,
        LanguageHolder languageB,
        DescVariant variantDescriptionB
    ) throws DataProviderException {
        DataProvider<VariantDescriptions> dataProvider = mock(DataProvider.class);
        when(dataProvider.getData(languageA.get())).thenReturn(getVariantDescriptions(variantDescriptionA));
        when(dataProvider.getData(languageB.get())).thenReturn(getVariantDescriptions(variantDescriptionB));
        return dataProvider;
    }

    private static VariantDescriptions getVariantDescriptions(DescVariant variant) {
        val descriptions = new VariantDescriptions();
        descriptions.getVariant().add(variant);
        return descriptions;
    }
}
