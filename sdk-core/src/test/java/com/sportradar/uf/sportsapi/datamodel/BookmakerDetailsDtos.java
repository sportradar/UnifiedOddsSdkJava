/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.uf.sportsapi.datamodel;

import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import javax.xml.datatype.DatatypeConfigurationException;

public class BookmakerDetailsDtos {

    private BookmakerDetailsDtos() {}

    public static BookmakerDetails bet365() throws DatatypeConfigurationException {
        BookmakerDetails bookmaker = new BookmakerDetails();
        final int hypotheticalBet365Id = 94332;
        bookmaker.setBookmakerId(hypotheticalBet365Id);
        bookmaker.setExpireAt(XmlGregorianCalendars.anyFutureDate());
        bookmaker.setResponseCode(ResponseCode.OK);

        return bookmaker;
    }

    public static BookmakerDetails notForRequestedEnvironment() {
        BookmakerDetails bookmaker = new BookmakerDetails();
        bookmaker.setResponseCode(ResponseCode.FORBIDDEN);
        return bookmaker;
    }
}
