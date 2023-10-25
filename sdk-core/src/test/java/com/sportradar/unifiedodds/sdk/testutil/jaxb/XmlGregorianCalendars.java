/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.jaxb;

import java.time.ZonedDateTime;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.val;

public class XmlGregorianCalendars {

    private XmlGregorianCalendars() {}

    public static XMLGregorianCalendar anyFutureDate() throws DatatypeConfigurationException {
        return tomorrow();
    }

    public static XMLGregorianCalendar tomorrow() throws DatatypeConfigurationException {
        val calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        val tomorrow = ZonedDateTime.now().plusDays(1);
        calendar.setYear(tomorrow.getYear());
        calendar.setMonth(tomorrow.getMonthValue());
        calendar.setDay(tomorrow.getDayOfMonth());
        return calendar;
    }
}
