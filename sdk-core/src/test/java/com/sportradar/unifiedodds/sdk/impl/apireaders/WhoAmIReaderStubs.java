/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public static WhoAmIReaderStubs.Builder simpleStub() {
        return new WhoAmIReaderStubs.Builder();
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

    @SuppressWarnings("HiddenField")
    public static class Builder {

        private Map<String, String> associatedSdkMdcContextMap;
        private Integer bookmakerId;
        private String virtualHost;

        public Builder withAnyMdcContextMap() {
            this.associatedSdkMdcContextMap = new HashMap<>();
            return this;
        }

        public Builder withBookmakerId(int bookmakerId) {
            this.bookmakerId = bookmakerId;
            return this;
        }

        public Builder withVirtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
            return this;
        }

        public WhoAmIReader build() {
            WhoAmIReader mock = mock(
                WhoAmIReader.class,
                invocation -> {
                    String methodName = invocation.getMethod().getName();
                    throw new UnsupportedOperationException(
                        "WhoAmIReader." + methodName + "() is not stubbed"
                    );
                }
            );

            ofNullable(associatedSdkMdcContextMap)
                .ifPresent(id -> doReturn(id).when(mock).getAssociatedSdkMdcContextMap());
            ofNullable(bookmakerId).ifPresent(id -> doReturn(id).when(mock).getBookmakerId());
            ofNullable(virtualHost).ifPresent(id -> doReturn(id).when(mock).getVirtualHost());

            return mock;
        }
    }
}
