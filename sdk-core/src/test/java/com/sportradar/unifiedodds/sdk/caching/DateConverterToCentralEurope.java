package com.sportradar.unifiedodds.sdk.caching;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings({ "HideUtilityClassConstructor" })
public class DateConverterToCentralEurope {

    public static Date convertFrom(Date date, ZoneId timeZone) {
        int centralEuropeOffsetSeconds = ZoneId
            .of("Europe/Berlin")
            .getRules()
            .getOffset(date.toInstant())
            .getTotalSeconds();
        int givenTimezoneOffsetSeconds = timeZone.getRules().getOffset(date.toInstant()).getTotalSeconds();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, -givenTimezoneOffsetSeconds + centralEuropeOffsetSeconds);

        return calendar.getTime();
    }
}
