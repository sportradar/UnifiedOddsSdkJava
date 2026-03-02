/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.generic.testing;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class Dates {

    private Dates() {}

    public static Date any() {
        return new Date();
    }

    public static Date date(int year, int month, int day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(year, month - 1, day, 0, 0, 0); // month-1 because Calendar is 0-based
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
