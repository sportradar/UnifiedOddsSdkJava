/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.utils;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

public class SdkHelperTest {

    private final int anyYear = 2023;
    private final int anyDay = 8;
    private final int anyHour = 9;
    private Calendar calendar;

    @Test
    public void sdkHelperShouldParseDateStringContainingYearMonthDayHourMinuteSecondsToDate()
        throws ParseException {
        setupCalendarWithExpectedDate();
        String dateString = "2023-05-08T09:00:00";
        Date actualDate = SdkHelper.toDate(dateString);

        Date expectedDate = calendar.getTime();
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void sdkHelperShouldParseRfc1223DateStringToDate() throws Exception {
        setupCalendarWithExpectedDate();
        String dateString = "Mon, 08 May 2023 09:00:00 GMT";

        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date actualDate = SdkHelper.toDate(dateString);

        Date expectedDate = calendar.getTime();
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void sdkHelperShouldParseRfc1036DateStringToDate() throws Exception {
        setupCalendarWithExpectedDate();
        String dateString = "Mon, 08-May-2023 09:00:00 GMT";

        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date actualDate = SdkHelper.toDate(dateString);

        Date expectedDate = calendar.getTime();
        assertEquals(expectedDate, actualDate);
    }

    private void setupCalendarWithExpectedDate() {
        calendar = GregorianCalendar.getInstance();
        calendar.set(anyYear, Calendar.MAY, anyDay, anyHour, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
