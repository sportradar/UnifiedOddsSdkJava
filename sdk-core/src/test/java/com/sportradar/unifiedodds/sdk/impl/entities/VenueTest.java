package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.unifiedodds.sdk.impl.Constants.SCHEDULE_MSG_URI;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SapiScheduleEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableVenueCi;
import com.sportradar.unifiedodds.sdk.entities.Venue;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.impl.XmlMessageReader;
import com.sportradar.utils.Urn;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MultipleStringLiterals" })
public class VenueTest {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final List<Locale> LOCALES = Arrays.asList(LOCALE);
    private static final String VENUE_ID = "sr:venue:26791";

    private SapiVenue sapiVenue;

    @BeforeEach
    public void setup() throws DeserializationException {
        SapiScheduleEndpoint sapiSchedule = XmlMessageReader.readMessageFromResource(SCHEDULE_MSG_URI);

        sapiVenue = sapiSchedule.getSportEvent().get(0).getVenue();
    }

    @Test
    public void parsesEntityFromXml() {
        VenueCi venueCi = new VenueCi(sapiVenue, LOCALE);

        Venue actual = new VenueImpl(venueCi, LOCALES);

        assertEquals(actual.getId(), Urn.parse(VENUE_ID));
        assertEquals(actual.getNames(), ImmutableMap.of(LOCALE, "Court 2"));
        assertEquals(actual.getCities(), ImmutableMap.of(LOCALE, "Newport Beach"));
        assertEquals(actual.getCountries(), ImmutableMap.of(LOCALE, "USA"));
        assertEquals(actual.getCountryCode(), "USA");
        assertEquals(actual.getState(), "CA");
    }

    @Test
    public void exportsImportsEntityFromCache() {
        VenueCi venueCi = new VenueCi(sapiVenue, LOCALE);

        ExportableVenueCi exportableVenueCi = venueCi.export(); //export to cache

        Venue actual = new VenueImpl(
            new VenueCi(exportableVenueCi), //import from cache
            LOCALES
        );

        assertEquals(actual.getId(), Urn.parse(VENUE_ID));
        assertEquals(actual.getNames(), ImmutableMap.of(LOCALE, "Court 2"));
        assertEquals(actual.getCities(), ImmutableMap.of(LOCALE, "Newport Beach"));
        assertEquals(actual.getCountries(), ImmutableMap.of(LOCALE, "USA"));
        assertEquals(actual.getCountryCode(), "USA");
        assertEquals(actual.getState(), "CA");
    }
}
