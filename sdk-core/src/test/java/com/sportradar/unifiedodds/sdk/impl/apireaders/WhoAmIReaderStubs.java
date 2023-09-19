/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import java.time.Duration;
import java.util.Date;

public class WhoAmIReaderStubs {

    private WhoAmIReaderStubs() {}

    public static WhoAmIReader anyBookmakerDetailsReader() {
        return emptyBookmakerDetailsReader();
    }

    public static WhoAmIReader emptyBookmakerDetailsReader() {
        WhoAmIReader reader = mock(WhoAmIReader.class);
        when(reader.getBookmakerDetails()).thenReturn(new EmptyBookmakerDetails());
        return reader;
    }

    public static WhoAmIReader readerProvidingBookmaker(BookmakerDetails bookmakerDetails) {
        WhoAmIReader reader = mock(WhoAmIReader.class);
        when(reader.getBookmakerDetails()).thenReturn(bookmakerDetails);
        return reader;
    }

    public static class EmptyBookmakerDetails implements BookmakerDetails {

        @Override
        public String getMessage() {
            return null;
        }

        @Override
        public Date getExpireAt() {
            return null;
        }

        @Override
        public int getBookmakerId() {
            return 0;
        }

        @Override
        public ResponseCode getResponseCode() {
            return null;
        }

        @Override
        public String getVirtualHost() {
            return null;
        }

        @Override
        public Duration getServerTimeDifference() {
            return null;
        }
    }
}
