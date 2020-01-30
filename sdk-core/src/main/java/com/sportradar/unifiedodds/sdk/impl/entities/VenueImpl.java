/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.unifiedodds.sdk.entities.Venue;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a sport event venue
 */
public class VenueImpl implements Venue {
    /**
     * A unique {@link URN} identifier representing the current {@link Venue} instance
     */
    private final URN id;

    /**
     * An unmodifiable {@link Map} containing venue's names in different languages
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<Locale, String> names;

    /**
     * An unmodifiable {@link Map} containing venue's city names in different languages
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<Locale, String> cities;

    /**
     * An unmodifiable {@link Map} containing venue's country names in different languages
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<Locale, String> countries;

    /**
     * The capacity of the venue associated with current {@link Venue} instance
     */
    private final Integer capacity;

    /**
     * The map coordinates specifying the exact location of the venue represented by current {@link Venue}
     */
    private final String coordinates;

    /**
     * The associated country code
     */
    private final String countryCode;

    private final String state;

    /**
     * Initializes a new intance of {@link VenueImpl}
     *
     * @param venueCI - a {@link VenueCI} used to build the instance
     * @param locales - a {@link List} specifying the supported languages
     */
    VenueImpl(VenueCI venueCI, List<Locale> locales) {
        Preconditions.checkNotNull(venueCI);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        this.id = venueCI.getId();
        this.capacity = venueCI.getCapacity();
        this.coordinates = venueCI.getCoordinates();
        this.countryCode = venueCI.getCountryCode();
        this.state = venueCI.getState();

        this.names = locales.stream()
                .filter(l -> venueCI.getName(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, venueCI::getName));
        this.cities = locales.stream()
                .filter(l -> venueCI.getCityName(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, venueCI::getCityName));
        this.countries = locales.stream()
                .filter(l -> venueCI.getCountryName(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, venueCI::getCountryName));
    }

    /**
     * Returns a unique {@link URN} identifier representing the current {@link Venue} instance
     *
     * @return - a unique {@link URN} identifier representing the current {@link Venue} instance
     */
    @Override
    public URN getId() {
        return id;
    }


    /**
     * Returns the name of the venue in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned venue name
     * @return - the name of the venue in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return names.get(locale);
    }

    /**
     * Returns the city name in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned city name
     * @return - the city name in the specified language
     */
    @Override
    public String getCity(Locale locale) {
        return cities.get(locale);
    }

    /**
     * Returns the country name in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned country name
     * @return - the country name in the specified language
     */
    @Override
    public String getCountry(Locale locale) {
        return countries.get(locale);
    }

    /**
     * Returns an unmodifiable {@link Map} containing venue's names in different languages
     *
     * @return - an unmodifiable {@link Map} containing venue's names in different languages
     */
    @Override
    public Map<Locale, String> getNames() {
        return names;
    }

    /**
     * Returns an unmodifiable {@link Map} containing venue's city names in different languages
     *
     * @return - an unmodifiable {@link Map} containing venue's city names in different languages
     */
    @Override
    public Map<Locale, String> getCities() {
        return cities;
    }

    /**
     * Returns an unmodifiable {@link Map} containing venue's country names in different languages
     *
     * @return - an unmodifiable {@link Map} containing venue's country names in different languages
     */
    @Override
    public Map<Locale, String> getCountries() {
        return countries;
    }

    /**
     * Returns the capacity of the venue associated with current {@link Venue} instance
     *
     * @return - the capacity of the venue, or a null if the capacity is not specified
     */
    @Override
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Returns the map coordinates specifying the exact location of the venue represented by current {@link Venue}
     *
     * @return - the map coordinates specifying the exact location of the venue
     */
    @Override
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the associated country code
     *
     * @return the associated country code
     */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getState() {
        return state;
    }

    /**
     * Returns a {@link String} describing the current {@link Venue} instance
     *
     * @return - a {@link String} describing the current {@link Venue} instance
     */
    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", names=" + names +
                ", cities=" + cities +
                ", countries=" + countries +
                ", capacity=" + capacity +
                ", coordinates='" + coordinates + '\'' +
                '}';
    }
}
