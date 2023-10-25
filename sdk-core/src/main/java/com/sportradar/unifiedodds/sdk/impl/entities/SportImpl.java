/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.entities.Category;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a sport
 */
public class SportImpl extends SportSummaryImpl implements Sport {

    /**
     * An unmodifiable {@link List} representing categories
     * which belong to the sport represented by the current instance
     */
    private final List<Category> categories;

    /**
     * Initializes a new instance of {@link SportImpl}
     *
     * @param id - an {@link Urn} uniquely identifying the sport represented by the current instance
     * @param names - a {@link Map} containing translated sport names
     * @param categories - a {@link List} representing categories
     *                     which belong to the sport represented by the current instance
     */
    public SportImpl(Urn id, Map<Locale, String> names, List<Category> categories) {
        super(id, names);
        Preconditions.checkNotNull(categories);

        this.categories = ImmutableList.copyOf(categories);
    }

    /**
     * Returns an unmodifiable {@link List} representing categories
     * which belong to the sport represented by the current instance
     *
     * @return  - an unmodifiable {@link List} representing categories
     * which belong to the sport represented by the current instance
     */
    @Override
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Returns a {@link String} describing the current {@link Sport} instance
     *
     * @return - a {@link String} describing the current {@link Sport} instance
     */
    @Override
    public String toString() {
        return "SportImpl{" + "categories=" + categories + "} " + super.toString();
    }
}
