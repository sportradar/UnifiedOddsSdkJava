/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;

public class XmlGregorianCalendarsTest {

    private final DateTime tomorrowsExpectation = DateTime.now().plusDays(1);

    @Test
    public void tomorrowIsTheDayAfterToday() throws DatatypeConfigurationException {
        XMLGregorianCalendar tomorrow = XmlGregorianCalendars.tomorrow();

        assertThat(tomorrow.getYear()).isEqualTo(tomorrowsExpectation.getYear());
        assertThat(tomorrow.getMonth()).isEqualTo(tomorrowsExpectation.getMonthOfYear());
        assertThat(tomorrow.getDay()).isEqualTo(tomorrowsExpectation.getDayOfMonth());
    }

    @Test
    public void futureDateIsAnyDayAfterToday() throws DatatypeConfigurationException {
        XMLGregorianCalendar calendar = XmlGregorianCalendars.anyFutureDate();

        LocalDate futureDate = toDate(calendar);

        assertThat(futureDate).isGreaterThan(LocalDate.now());
    }

    private LocalDate toDate(XMLGregorianCalendar calendar) {
        LocalDate dateTime = new LocalDate();

        return dateTime
            .withYear(calendar.getYear())
            .withMonthOfYear(calendar.getMonth())
            .withDayOfMonth(calendar.getDay());
    }
}
