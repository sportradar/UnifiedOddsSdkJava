package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SAPIScheduleEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIVenue;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableVenueCI;
import com.sportradar.unifiedodds.sdk.entities.Venue;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.impl.XmlMessageReader;
import com.sportradar.utils.URN;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.sportradar.unifiedodds.sdk.impl.Constants.SCHEDULE_MSG_URI;
import static org.junit.Assert.assertEquals;

public class VenueTest {
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);
    private static final String VENUE_ID = "sr:venue:26791";

    private SAPIVenue sapiVenue;

    @Before
    public void setup() throws DeserializationException {
        SAPIScheduleEndpoint sapiSchedule = XmlMessageReader.readMessageFromResource(SCHEDULE_MSG_URI);

        sapiVenue = sapiSchedule.getSportEvent().get(0).getVenue();
    }

    @Test
    public void parsesEntityFromXml() {
        VenueCI venueCI = new VenueCI(sapiVenue, LOCALE);

        Venue actual = new VenueImpl(venueCI, LOCALES);

        assertEquals(actual.getId(), URN.parse(VENUE_ID));
        assertEquals(actual.getNames(), ImmutableMap.of(LOCALE, "Court 2"));
        assertEquals(actual.getCities(), ImmutableMap.of(LOCALE, "Newport Beach"));
        assertEquals(actual.getCountries(), ImmutableMap.of(LOCALE, "USA"));
        assertEquals(actual.getCountryCode(), "USA");
        assertEquals(actual.getState(), "CA");
    }

    @Test
    public void exportsImportsEntityFromCache() {
        VenueCI venueCI = new VenueCI(sapiVenue, LOCALE);

        ExportableVenueCI exportableVenueCI = venueCI.export(); //export to cache

        Venue actual = new VenueImpl(
                new VenueCI(exportableVenueCI), //import from cache
                LOCALES
        );

        assertEquals(actual.getId(), URN.parse(VENUE_ID));
        assertEquals(actual.getNames(), ImmutableMap.of(LOCALE, "Court 2"));
        assertEquals(actual.getCities(), ImmutableMap.of(LOCALE, "Newport Beach"));
        assertEquals(actual.getCountries(), ImmutableMap.of(LOCALE, "USA"));
        assertEquals(actual.getCountryCode(), "USA");
        assertEquals(actual.getState(), "CA");
    }
}
