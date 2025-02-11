/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.Outcome;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 24/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "LineLength" })
abstract class OutcomeImpl implements Outcome {

    private final String id;
    private final NameProvider nameProvider;
    private final OutcomeDefinition outcomeDefinition;
    private final Locale defaultLocale;

    OutcomeImpl(
        String id,
        NameProvider nameProvider,
        OutcomeDefinition outcomeDefinition,
        Locale defaultLocale
    ) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(nameProvider);
        Preconditions.checkNotNull(outcomeDefinition);
        Preconditions.checkNotNull(defaultLocale);

        this.id = id;
        this.nameProvider = nameProvider;
        this.outcomeDefinition = outcomeDefinition;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Returns the outcome id
     *
     * @return - the outcome id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns the outcome name
     *
     * @return - the outcome name
     */
    @Override
    public String getName() {
        return nameProvider.getOutcomeName(id, defaultLocale);
    }

    @Override
    public String getName(Locale locale) {
        return nameProvider.getOutcomeName(id, locale);
    }

    /**
     * Returns the description of this outcome
     *
     * @return - the description of this outcome
     */
    @Override
    public OutcomeDefinition getOutcomeDefinition() {
        return outcomeDefinition;
    }

    /**
     * @param locales the list of {@link Locale} in which the name should be returned
     * @return - the names of the market translated in the specified {@link Locale} (specifier placeholders are replaced with actual
     * values)
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        return nameProvider.getOutcomeNames(id, locales);
    }
}
