/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.Manager;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.ManagerCi;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * An implementation of the {@link Manager} interface
 */
public class ManagerImpl implements Manager {

    private final ManagerCi managerCi;

    public ManagerImpl(ManagerCi managerCi) {
        Preconditions.checkNotNull(managerCi);

        this.managerCi = managerCi;
    }

    /**
     * Returns the manager identifier
     *
     * @return the manager identifier
     */
    @Override
    public Urn getId() {
        return managerCi.getId();
    }

    /**
     * Returns the translated manager name
     *
     * @param locale the locale in which the name should be provided
     * @return the translated manager name
     */
    @Override
    public String getName(Locale locale) {
        return managerCi.getNames().get(locale);
    }

    /**
     * Returns the translated manager name
     */
    @Override
    public Map<Locale, String> getNames() {
        return managerCi.getNames();
    }

    /**
     * Returns the translated nationality
     *
     * @param locale the locale in which the nationality should be provided
     * @return the translated nationality
     */
    @Override
    public String getNationality(Locale locale) {
        return managerCi.getNationality(locale);
    }

    /**
     * Returns the country code
     *
     * @return the country code
     */
    @Override
    public String getCountryCode() {
        return managerCi.getCountryCode();
    }
}
