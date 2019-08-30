/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIVenue;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableVenueCI;
import com.sportradar.utils.URN;

import java.util.*;

/**
 * A venue representation used by caching components
 */
public class VenueCI extends SportEntityCI implements ExportableCacheItem {
    /**
     * A {@link HashMap} containing venue name in different languages
     */
    private final HashMap<Locale, String> names;

    /**
     * A {@link HashMap} containing city of the venue in different languages
     */
    private final HashMap<Locale, String> cityNames;

    /**
     *A {@link HashMap} containing country of the venue in different languages
     */
    private final HashMap<Locale, String> countryNames;

    /**
     * The capacity of the venue
     */
    private Integer capacity;

    /**
     * The associated country code
     */
    private String countryCode;

    /**
     * The map coordinates specifying the exact location of the venue
     */
    private String coordinates;

    private final List<Locale> cachedLocales;

    /**
     * Initializes a new instance of the {@link VenueCI} class
     *
     * @param venue - {@link SAPIVenue} containing information about the venue
     * @param locale - {@link Locale} specifying the language of the <i>venue</i>
     */
    public VenueCI(SAPIVenue venue, Locale locale) {
        super(URN.parse(venue.getId()));

        Preconditions.checkNotNull(venue);
        Preconditions.checkNotNull(locale);

        names = new HashMap<>();
        cityNames = new HashMap<>();
        countryNames = new HashMap<>();
        cachedLocales = Collections.synchronizedList(new ArrayList<>());

        merge(venue, locale);
    }

    public VenueCI(ExportableVenueCI exportable) {
        super(URN.parse(exportable.getId()));
        Preconditions.checkNotNull(exportable);

        names = new HashMap<>(exportable.getNames());
        cityNames = new HashMap<>(exportable.getCityNames());
        countryNames = new HashMap<>(exportable.getCountryNames());
        capacity = exportable.getCapacity();
        countryCode = exportable.getCountryCode();
        coordinates = exportable.getCoordinates();
        cachedLocales = Collections.synchronizedList(new ArrayList<>(exportable.getCachedLocales()));
    }

    /**
     * Merges the information from the provided {@link SAPIVenue} into the current instance
     *
     * @param venue - {@link SAPIVenue} containing information about the venue
     * @param locale - {@link Locale} specifying the language of the <i>venue</i>
     */
    public void merge(SAPIVenue venue, Locale locale) {
        Preconditions.checkNotNull(venue);
        Preconditions.checkNotNull(locale);

        capacity = venue.getCapacity();
        coordinates = venue.getMapCoordinates();
        names.put(locale, venue.getName());
        cityNames.put(locale, venue.getCityName());
        countryNames.put(locale, venue.getCountryName());
        countryCode = venue.getCountryCode();

        cachedLocales.add(locale);
    }

    /**
     * Returns the name of the venue in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned name
     * @return - The name of the venue in the specified language if it exists. Null otherwise.
     */
    public String getName(Locale locale) {
        return names.getOrDefault(locale, null);
    }

    /**
     * Returns the city name of the venue in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned name
     * @return - The city name of the venue in the specified language if it exists. Null otherwise.
     */
    public String getCityName(Locale locale) {
        return cityNames.getOrDefault(locale, null);
    }

    /**
     * Returns the country name of the venue in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned name
     * @return - The country name of the venue in the specified language if it exists. Null otherwise.
     */
    public String getCountryName(Locale locale) {
        return countryNames.getOrDefault(locale, null);
    }

    /**
     * Returns the capacity of the venue associated with current instance, or a null
     * reference if the capacity is not specified
     *
     * @return - the capacity of the venue associated with current instance, or a null
     * reference if the capacity is not specified
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Returns the map coordinates specifying the exact location of the venue represented by current instance
     *
     * @return - the map coordinates specifying the exact location of the venue represented by current instance
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the associated country code
     *
     * @return the associated country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    public boolean hasTranslationsFor(List<Locale> locales) {
        Preconditions.checkNotNull(locales);

        return cachedLocales.containsAll(locales);
    }

    /**
     * Export item's properties
     *
     * @return An {@link ExportableCI} instance containing all relevant properties
     */
    @Override
    public ExportableCI export() {
        return new ExportableVenueCI(
                getId().toString(),
                new HashMap<>(names),
                new HashMap<>(cityNames),
                new HashMap<>(countryNames),
                capacity,
                countryCode,
                coordinates,
                new ArrayList<>(cachedLocales)
        );
    }
}
