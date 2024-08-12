/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.uf.sportsapi.datamodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BookmakerDetailsDtosTest {

    @Nested
    public class Bet365 {

        private BookmakerDetails bookmaker = BookmakerDetailsDtos.bet365();

        public Bet365() throws DatatypeConfigurationException {}

        @Test
        public void containConstantPositiveId() {
            final int hypotheticalBet365Id = 94332;
            assertThat(bookmaker.getBookmakerId()).isEqualTo(hypotheticalBet365Id);
        }

        @Test
        public void expiresInMoreThanYear() {
            val expiration = bookmaker.getExpireAt().toGregorianCalendar().toZonedDateTime();

            assertThat(expiration).isAfter(ZonedDateTime.now());
        }

        @Test
        public void isSuccessfullyFetched() {
            assertThat(bookmaker.getResponseCode()).isEqualTo(ResponseCode.OK);
        }
    }

    public static class NotForRequestedEnvironment {

        private BookmakerDetails bookmaker = BookmakerDetailsDtos.notForRequestedEnvironment();

        public NotForRequestedEnvironment() throws DatatypeConfigurationException {}

        @Test
        public void requestWasDenied() {
            assertThat(bookmaker.getResponseCode()).isEqualTo(ResponseCode.FORBIDDEN);
        }
    }
}
