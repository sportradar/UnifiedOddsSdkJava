/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.getSapiSport;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.uf.sportsapi.datamodel.SapiSportCategoriesEndpoint;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import lombok.SneakyThrows;
import lombok.val;

public class SportsCategoriesDataProviders {

    @SneakyThrows
    public static DataProvider<SapiSportCategoriesEndpoint> providing(
        LanguageHolder language,
        String sportId,
        SapiSportCategoriesEndpoint categories
    ) {
        DataProvider<SapiSportCategoriesEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(categories).when(dataProvider).getData(language.get(), sportId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportId);
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("LambdaBodyLength")
    public static DataProvider<SapiSportCategoriesEndpoint> providingAllCategories(LanguageHolder language) {
        DataProvider<SapiSportCategoriesEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );

        SapiCategories
            .allCategories(language)
            .forEach((sportId, categories) -> {
                try {
                    val response = sapiCategoriesEndpointResponse(language, sportId, categories);
                    doReturn(response).when(dataProvider).getData(language.get(), sportId.toString());
                    doReturn(anyHttpUrl().toString())
                        .when(dataProvider)
                        .getFinalUrl(language.get(), sportId.toString());
                } catch (DataProviderException e) {
                    throw new RuntimeException(e);
                }
            });
        return dataProvider;
    }

    private static SapiSportCategoriesEndpoint sapiCategoriesEndpointResponse(
        LanguageHolder language,
        Urn sportId,
        List<SapiCategory> categories
    ) {
        val response = new SapiSportCategoriesEndpoint();
        response.setCategories(new com.sportradar.uf.sportsapi.datamodel.SapiCategories());
        response.getCategories().getCategory().addAll(categories);
        response.setSport(getSapiSport(sportId, language));
        return response;
    }
}
