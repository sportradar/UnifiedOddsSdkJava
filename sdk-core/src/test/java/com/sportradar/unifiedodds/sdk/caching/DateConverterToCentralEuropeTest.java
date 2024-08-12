/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber" })
public class DateConverterToCentralEuropeTest {

    @Test
    public void shouldTranslateEasterEuropeanDateToCentralEuropean() {
        Date centralEuropeanTime = new Date(118, 4, 15, 9, 30);
        Date easterEuropeanTime = new Date(118, 4, 15, 10, 30);
        assertEquals(
            centralEuropeanTime,
            DateConverterToCentralEurope.convertFrom(easterEuropeanTime, ZoneId.of("Europe/Riga"))
        );
    }

    @Test
    public void shouldTranslateLondonTimeToCentralEuropean() {
        Date centralEuropeanTime = new Date(118, 4, 15, 9, 30);
        Date britishTime = new Date(118, 4, 15, 8, 30);
        assertEquals(
            centralEuropeanTime,
            DateConverterToCentralEurope.convertFrom(britishTime, ZoneId.of("Europe/London"))
        );
    }

    @Test
    public void shouldKeepCentralEuropeanTimeUnchanged() {
        Date centralEuropeanTime = new Date(118, 4, 15, 9, 30);
        assertEquals(
            centralEuropeanTime,
            DateConverterToCentralEurope.convertFrom(centralEuropeanTime, ZoneId.of("Europe/Rome"))
        );
    }
}
