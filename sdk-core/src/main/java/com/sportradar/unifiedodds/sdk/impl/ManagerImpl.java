/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.ManagerCI;
import com.sportradar.unifiedodds.sdk.entities.Manager;
import com.sportradar.utils.URN;
import java.util.Locale;

/**
 * An implementation of the {@link Manager} interface
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class ManagerImpl implements Manager {

    private final ManagerCI managerCI;

    public ManagerImpl(ManagerCI managerCI) {
        Preconditions.checkNotNull(managerCI);

        this.managerCI = managerCI;
    }

    /**
     * Returns the manager identifier
     *
     * @return the manager identifier
     */
    @Override
    public URN getId() {
        return managerCI.getId();
    }

    /**
     * Returns the translated manager name
     *
     * @param locale the locale in which the name should be provided
     * @return the translated manager name
     */
    @Override
    public String getName(Locale locale) {
        return managerCI.getName(locale);
    }

    /**
     * Returns the translated nationality
     *
     * @param locale the locale in which the nationality should be provided
     * @return the translated nationality
     */
    @Override
    public String getNationality(Locale locale) {
        return managerCI.getNationality(locale);
    }

    /**
     * Returns the country code
     *
     * @return the country code
     */
    @Override
    public String getCountryCode() {
        return managerCI.getCountryCode();
    }
}
