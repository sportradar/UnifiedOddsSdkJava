/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.DrawResult;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * A basic implementation of the {@link DrawResult}
 */
public class DrawResultImpl implements DrawResult {

    private final Integer value;
    private final Map<Locale, String> names;

    DrawResultImpl(Integer value, Map<Locale, String> names) {
        Preconditions.checkNotNull(value);

        this.value = value;
        this.names = names;
    }

    /**
     * Returns the value of the draw
     *
     * @return the value of the draw
     */
    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * Returns the name of the draw result
     *
     * @param locale the {@link Locale} in which the data should be provided
     * @return the name of the draw result
     */
    @Override
    public String getName(Locale locale) {
        return names == null ? null : names.get(locale);
    }

    /**
     * Returns the name of the draw result
     */
    @Override
    public Map<Locale, String> getNames() {
        return ImmutableMap.copyOf(names);
    }
}
