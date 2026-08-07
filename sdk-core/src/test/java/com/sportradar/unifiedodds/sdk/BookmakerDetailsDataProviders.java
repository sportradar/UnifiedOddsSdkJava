/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.DataWrapper;
import java.util.Locale;
import org.mockito.Mockito;

public class BookmakerDetailsDataProviders {

    private BookmakerDetailsDataProviders() {}

    public static DataProvider<BookmakerDetails> providing(BookmakerDetails bookmakerDetails)
        throws DataProviderException {
        DataProvider<BookmakerDetails> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(new DataWrapper<>(bookmakerDetails, null))
            .when(dataProvider)
            .getDataWithAdditionalInfo(any(Locale.class));
        return dataProvider;
    }

    @SuppressWarnings("unchecked")
    public static DataProvider<BookmakerDetails> failingWith(DataProviderException exception)
        throws DataProviderException {
        DataProvider<BookmakerDetails> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        Mockito.doThrow(exception).when(dataProvider).getDataWithAdditionalInfo(any(Locale.class));
        return dataProvider;
    }
}
