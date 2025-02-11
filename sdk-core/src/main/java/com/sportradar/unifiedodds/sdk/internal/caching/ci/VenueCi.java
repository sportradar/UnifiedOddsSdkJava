/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableVenueCi;
import com.sportradar.utils.Urn;
import java.util.*;

/**
 * A venue representation used by caching components
 */
@SuppressWarnings({ "IllegalType" })
public class VenueCi extends SportEntityCi {

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

    private String state;

    private CoursesCi courses;

    private final List<Locale> cachedLocales;

    /**
     * Initializes a new instance of the {@link VenueCi} class
     *
     * @param venue - {@link SapiVenue} containing information about the venue
     * @param locale - {@link Locale} specifying the language of the <i>venue</i>
     */
    public VenueCi(SapiVenue venue, Locale locale) {
        super(Urn.parse(venue.getId()));
        Preconditions.checkNotNull(venue);
        Preconditions.checkNotNull(locale);

        names = new HashMap<>();
        cityNames = new HashMap<>();
        countryNames = new HashMap<>();
        cachedLocales = Collections.synchronizedList(new ArrayList<>());

        this.courses = new CoursesCi(venue.getCourse(), locale);
        merge(venue, locale);
    }

    public VenueCi(ExportableVenueCi exportable) {
        super(Urn.parse(exportable.getId()));
        Preconditions.checkNotNull(exportable);

        names = new HashMap<>(exportable.getNames());
        cityNames = new HashMap<>(exportable.getCityNames());
        countryNames = new HashMap<>(exportable.getCountryNames());
        capacity = exportable.getCapacity();
        countryCode = exportable.getCountryCode();
        coordinates = exportable.getCoordinates();
        state = exportable.getState();
        this.courses = CoursesCi.importFrom(exportable.getCourses());
        cachedLocales = Collections.synchronizedList(new ArrayList<>(exportable.getCachedLocales()));
    }

    /**
     * Merges the information from the provided {@link SapiVenue} into the current instance
     *
     * @param venue - {@link SapiVenue} containing information about the venue
     * @param locale - {@link Locale} specifying the language of the <i>venue</i>
     */
    public void merge(SapiVenue venue, Locale locale) {
        Preconditions.checkNotNull(venue);
        Preconditions.checkNotNull(locale);

        capacity = venue.getCapacity();
        coordinates = venue.getMapCoordinates();
        names.put(locale, venue.getName());
        cityNames.put(locale, venue.getCityName());
        countryNames.put(locale, venue.getCountryName());
        countryCode = venue.getCountryCode();
        state = venue.getState();
        courses.merge(venue.getCourse(), locale);
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

    /**
     * Returns state/province of the country
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * Returns state/province of the country
     *
     * @return state
     */
    public List<CourseCi> getCourses() {
        return courses.get();
    }

    public boolean hasTranslationsFor(List<Locale> locales) {
        Preconditions.checkNotNull(locales);

        return cachedLocales.containsAll(locales);
    }

    public ExportableVenueCi export() {
        return new ExportableVenueCi(
            getId().toString(),
            new HashMap<>(names),
            new HashMap<>(cityNames),
            new HashMap<>(countryNames),
            capacity,
            countryCode,
            coordinates,
            new ArrayList<>(cachedLocales),
            state,
            courses.export()
        );
    }
}
