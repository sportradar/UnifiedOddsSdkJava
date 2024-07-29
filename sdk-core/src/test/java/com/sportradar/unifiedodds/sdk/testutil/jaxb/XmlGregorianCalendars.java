/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.jaxb;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.SneakyThrows;
import lombok.val;

public class XmlGregorianCalendars {

    private XmlGregorianCalendars() {}

    public static XMLGregorianCalendar anyFutureDate() {
        return tomorrow();
    }

    @SneakyThrows
    public static XMLGregorianCalendar tomorrow() {
        val calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        val tomorrow = ZonedDateTime.now().plusDays(1);
        calendar.setYear(tomorrow.getYear());
        calendar.setMonth(tomorrow.getMonthValue());
        calendar.setDay(tomorrow.getDayOfMonth());
        return calendar;
    }

    @SneakyThrows
    public static XMLGregorianCalendar forDate(LocalDate localDate) {
        val calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        calendar.setYear(localDate.getYear());
        calendar.setMonth(localDate.getMonthValue());
        calendar.setDay(localDate.getDayOfMonth());
        return calendar;
    }

    @SneakyThrows
    public static XMLGregorianCalendar now() {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar();
    }
}
