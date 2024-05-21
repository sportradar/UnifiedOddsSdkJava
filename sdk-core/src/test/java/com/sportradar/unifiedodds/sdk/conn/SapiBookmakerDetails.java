/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import lombok.SneakyThrows;

public class SapiBookmakerDetails {

    @SneakyThrows
    public static BookmakerDetails valid() {
        final BookmakerDetails bookmakerDetails = new BookmakerDetails();
        int anyBookmakerId = 1;
        bookmakerDetails.setBookmakerId(anyBookmakerId);
        bookmakerDetails.setExpireAt(XmlGregorianCalendars.anyFutureDate());
        bookmakerDetails.setVirtualHost("/unifiedfeed/" + anyBookmakerId);
        bookmakerDetails.setResponseCode(ResponseCode.OK);
        return bookmakerDetails;
    }
}
