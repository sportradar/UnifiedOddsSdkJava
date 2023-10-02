/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiReferee;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableRefereeCi;
import com.sportradar.utils.Urn;
import java.util.HashMap;
import java.util.Locale;

/**
 * A referee representation used by caching components
 */
@SuppressWarnings({ "IllegalType" })
public class RefereeCi extends SportEntityCi {

    /**
     * A {@link HashMap} containing referee nationality in different languages
     */
    private final HashMap<Locale, String> nationalities;

    /**
     * The name of the referee
     */
    private String name;

    /**
     * Initializes a new instance of the {@link RefereeCi} class
     *
     * @param referee - {@link SapiReferee} containing information about the referee
     * @param locale - {@link Locale} specifying the language of the <i>referee</i>
     */
    RefereeCi(SapiReferee referee, Locale locale) {
        super(Urn.parse(referee.getId()));
        Preconditions.checkNotNull(referee);
        Preconditions.checkNotNull(locale);

        nationalities = new HashMap<>();
        merge(referee, locale);
    }

    RefereeCi(ExportableRefereeCi exportable) {
        super(Urn.parse(exportable.getId()));
        nationalities = new HashMap<>(exportable.getNationalities());
        name = exportable.getName();
    }

    /**
     * Merges the information from the provided {@link SapiReferee} into the current instance
     *
     * @param referee - {@link SapiReferee} containing information about the referee
     * @param locale - {@link Locale} specifying the language of the <i>referee</i>
     */
    public void merge(SapiReferee referee, Locale locale) {
        Preconditions.checkNotNull(referee);
        Preconditions.checkNotNull(locale);

        name = referee.getName();
        nationalities.put(locale, referee.getNationality());
    }

    /**
     * Returns the name of the referee
     *
     * @return - the name of the referee
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the nationality of the referee in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned nationality
     * @return - The nationality of the referee in the specified language if it exists. Null otherwise.
     */
    public String getNationality(Locale locale) {
        return nationalities.getOrDefault(locale, null);
    }

    public ExportableRefereeCi export() {
        return new ExportableRefereeCi(getId().toString(), new HashMap<>(nationalities), name);
    }
}
